/**
 * Copyright (c) <2016> <hasder>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 	
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
*/
package com.eislab.af.translator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.eislab.af.translator.data.TranslatorSetup;


@Path("/")
@Singleton
public class TranslatorService implements Observer {

	Map<Integer, Translator_hub> hubs = new HashMap<Integer, Translator_hub>();
	
	public TranslatorService() {
		
	}	
	
	@Path("/translator")
	@GET
	@Produces(MediaType.APPLICATION_XML)
    public Response getTranslator() {
		Response response;
		
//		Translator_hub hub = new Translator_hub();
//		
//		
//		
//		hubs.put(1234,hub);
//		TranslatorSetup setup = new TranslatorSetup();
//		setup.setConsumerName("testconsumer");
//		setup.setProviderName("providerName");
//		response = Response.ok(setup).build();
		
		//Map<Integer,String> hubResponse = new HashMap<Integer,String>(); // No need of Map as we can not build automatically the response
		String hubResponse = "<translatorList>";
		if (hubs.isEmpty()) {
			hubResponse += "null";
		}
		else {
			hubResponse += "\n";
			for (Entry<Integer, Translator_hub> entry : hubs.entrySet()) {
				//hubResponse.put(entry.getKey(), entry.getValue().getPSpokeAddress());
				hubResponse += "<translatorId>" + entry.getKey() + "</translatorId><translatorAddress>" + entry.getValue().getPSpokeAddress()+"</translatorAddress>+\n";
			}
		}
		hubResponse +="</translatorList>";
		
		response = Response.ok(hubResponse).build();// No need to specify XML Mediatype as the runtime will set it with @Produces
		return response;
	
	}
	
	@Path("/translator")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
    public Response postTranslator(TranslatorSetup setup) {
		Response response;
		
		//--------- Check if a hub which satisfies the  request already exists ----------
		//make a fingerprint based on service provider name and service consumer name combined and hashed.
		//combination of providername and consumername will be a fully unique pairing. THIS MAY CHANGE IN THE FUTURE AND DO IT BASED ON TYPE + ADDRESS
		int fingerprint = (setup.getProviderName() + setup.getConsumerName()).hashCode();
		
		if(!hubs.isEmpty() && hubs.containsKey(fingerprint)) {
			Translator_hub existingHub = hubs.get(fingerprint);
			response = Response.ok("<translationendpoint><id>" + existingHub.getId() + "</id><ip>"+ existingHub.getPSpokeIp() +"</ip><port>"+ existingHub.getPSpokePort() +"</port></translationendpoint>").build();
		} else {
		
			Translator_hub hub = new Translator_hub(this);
			System.out.println("post to Translator received: ClientSpoke type:" + setup.getProviderType() + " ServerSpoke type: " + setup.getConsumerType());
			
			//cSpoke is the spoke connected to the service provider endpoint
			String cSpoke_ProviderName = 	setup.getProviderName().substring(0).toLowerCase();
			String cSpoke_ProviderType = 	setup.getProviderType().substring(0).toLowerCase();
			String cSpoke_ProviderAddress = setup.getProviderAddress().substring(0).toLowerCase();
			
			//pSpoke is the spoke connected to the service consumer endpoint
			String pSpoke_ConsumerName = 	setup.getConsumerName().substring(0).toLowerCase();
			String pSpoke_ConsumerType = 	setup.getConsumerType().substring(0).toLowerCase();
			String pSpoke_ConsumerAddress = setup.getConsumerAddress().substring(0).toLowerCase();
			
			hub.setPSpoke_ConsumerName(pSpoke_ConsumerName);
			hub.setpSpoke_ConsumerType(pSpoke_ConsumerType);
			hub.setpSpoke_ConsumerAddress(pSpoke_ConsumerAddress);
			hub.setCSpoke_ProviderName(cSpoke_ProviderName);
			hub.setcSpoke_ProviderType(cSpoke_ProviderType);
			hub.setCSpoke_ProviderAddress(cSpoke_ProviderAddress);//hub.setServiceEndpoint("coap://127.0.0.1:5692/");
			
			int id = hub.getId();
			
			hub.setFingerprint(fingerprint);
			int error = 0;
			try {
				hub.online();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				error = 1;
			}
			
			if(error==0) {
				hubs.put(fingerprint, hub);
				
				/*
				 * Why send "id" if in the getTranslator, arg translatorid, what it used is the "fingerprint"
				 */
				//response = Response.ok("<translatorId>newtranslator=" + id + "</translatorId><translatorAddress>"+ hub.getPSpokeAddress() +"</translatorAddress>").build();
				response = Response.ok("<translationendpoint><id>" + id + "</id><ip>"+ hub.getPSpokeIp() +"</ip><port>"+ hub.getPSpokePort() +"</port></translationendpoint>").build();
			} else {
				response = Response.serverError().build();
			}
		}
		return response;
	
	}
	
	
	@Path("/translator/{translatorid}")
	@GET
    public Response getTranslator( @PathParam("translatorid") int translatorid) {
		Response response;
		Translator_hub hub = null;
		
		hub = hubs.get(translatorid); // translatorid is the fingerprint stored when creating hub, which user never receives!
		
		if(hub != null) {	
			response = Response.ok("<translatorId>" + translatorid + "</translatorId><translatorAddress>"+ hub.getPSpokeAddress() +"</translatorAddress>").build();
		} else {
			response = Response.ok("<translatorId>" + translatorid + "</translatorId><error>" + "Error hub does not exist" + "</error>").build();
		}
		
		return response;
	
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		hubs.remove((int)arg1);
		System.out.println("Cleanup hub: " + (int)arg1);
	}
	
}
