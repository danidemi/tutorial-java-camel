package com.danidemi.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.danidemi.jlubricant.utils.properties.PropertiesUtils;

import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.FFMQServer;
import net.timewalker.ffmq3.utils.Settings;

public class FFMQ1 {

	public static void main(String[] args) {
		try {
			fofo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void fofo() throws JMSException, InterruptedException, IOException {
		
		File baseDirectory = new File( FileUtils.getTempDirectory(), "ffmq" );
		baseDirectory.mkdirs();
		
		File destinationDefinitionsDirectory = new File(baseDirectory, "destination_definition");
		File templatesDirectory = new File(baseDirectory, "templates");
		File defaultDataDirectory = new File(baseDirectory, "data");
		
		File templateMapping = new File(baseDirectory, "templates.mappings");
		FileUtils.copyInputStreamToFile(FFMQ1.class.getResourceAsStream("/templates.mapping"), templateMapping);
		
		com.danidemi.jlubricant.utils.file.FileUtils.mkdirs(destinationDefinitionsDirectory, templatesDirectory, defaultDataDirectory);
		
		Properties adminQueue = new Properties();
		adminQueue.setProperty("name", "ADMIN_QUEUE_TEMPLATE");
		adminQueue.setProperty("persistentStore.initialBlockCount", "0");
		adminQueue.setProperty("memoryStore.maxMessages", "1000");
		PropertiesUtils.storeToFile( adminQueue, new File(templatesDirectory, "queue-" + "ADMIN_QUEUE_TEMPLATE" + ".properties") );
		
		
		Properties props = new Properties();
		props.load( FFMQ1.class.getResourceAsStream("/server.properties") );
		
		Properties usedProps = new Properties(props);
		usedProps.setProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, destinationDefinitionsDirectory.getAbsolutePath());
		usedProps.setProperty(FFMQCoreSettings.TEMPLATES_DIR, templatesDirectory.getAbsolutePath());
		usedProps.setProperty(FFMQCoreSettings.TEMPLATE_MAPPING_FILE, templateMapping.getAbsolutePath());
		usedProps.setProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, defaultDataDirectory.getAbsolutePath());
		
		FFMQServer server = new FFMQServer("the-engine", new Settings(PropertiesUtils.flatten( usedProps )));
		
		boolean started = false;
		try{
			started = server.start();
			if(started){
				Thread.sleep(1000 * 5);				
			}
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			if(started){
				server.shutdown();										
			}
		}
		
		
		
	}
	

	
}
