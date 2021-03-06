/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;

import com.mycompany.Utilidades.JSONSerializable;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
/**
 *
 * @author dark_
 */
public class CreadoDeRespuestas {
    public static Response createResponse( Response.Status status  ) {
		JSONObject jsonObject = new JSONObject();
		
		try {
			jsonObject.put( "message", status.toString() );
		}
		catch( JSONException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( Response.Status.INTERNAL_SERVER_ERROR ).build();
		}
		
		return Response.status( status ).entity( jsonObject.toString() ).build();
	}
	
	public static Response createResponse( Response.Status status, String message ) {
		JSONObject jsonObject = new JSONObject();
		
		try {
			jsonObject.put( "message", message );
		}
		catch( JSONException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( Response.Status.INTERNAL_SERVER_ERROR ).build();
		}
		
		return Response.status( status ).entity( jsonObject.toString() ).build();
	}
	
	public static Response createResponse( Response.Status status, JSONSerializable json ) throws JSONException {
		return Response.status( status ).entity( json.toJson().toString() ).build();
	}
	
	public static Response createResponse( Response.Status status, List<JSONSerializable> json ) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		
		for( int i = 0; i < json.size(); i++ ) {
			jsonArray.put( json.get(i).toJson() );
		}
		
		return Response.status( status ).entity( jsonArray.toString() ).build();
	}
	
	public static Response createResponse( Response.Status status, Map<String,Object> map ) {
		JSONObject jsonObject = new JSONObject();
		
		try {
			for( Map.Entry<String,Object> entry : map.entrySet() ) {
				jsonObject.put( entry.getKey(), entry.getValue() );
			}
		}
		catch( JSONException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( Response.Status.INTERNAL_SERVER_ERROR ).build();
		}
		
		return Response.status( status ).entity( jsonObject.toString() ).build();
}
}
