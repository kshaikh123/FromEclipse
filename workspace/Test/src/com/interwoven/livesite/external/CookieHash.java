package com.interwoven.livesite.external;

import com.interwoven.livesite.common.util.Hashtable;
import com.interwoven.livesite.common.xml.XmlEmittable;
import com.interwoven.livesite.common.xml.XmlEmittableUtils;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CookieHash extends Hashtable
  implements XmlEmittable
{
  private static final long serialVersionUID = 1L;
  private static final String COOKIES = "Cookies";
  private static final String COOKIE = "Cookie";
  private Cookie mSession = null;

  public CookieHash()
  {
  }

  public CookieHash(Cookie[] cookies)
  {
    if (cookies != null)
    {
      for (int cnt = 0; cnt < cookies.length; cnt++)
      {
        String name = cookies[cnt].getName();

        if (name.equals("JSESSIONID"))
        {
          this.mSession = cookies[cnt];
        }
        else
        {
          put(cookies[cnt].getName(), cookies[cnt]);
        }
      }
    }
  }

  public Cookie getCookie(String name)
  {
    return (Cookie)get(name);
  }

  public String getValue(String name)
  {
	  try{
		  return ((Cookie)get(name)).getValue();
	  }
	  catch(Exception e){
		  return "null";
	  }
  }
  
  public String getValueCustom(String name)
  {
	  try{
		  return ((Cookie)get(name)).getValue();
	  }
	  catch(Exception e){
		  return "null";
	  }
  }
  public String getValueWithDelimiter(String input)
  {
	  try{
		  String paramDelimiter = "\\%\\^\\^\\%";
		  String [] inputParams = input.split(paramDelimiter);
		  String name = inputParams[0];
		  String delimiterTemp = inputParams[1];
		  String delimiter = escapeSequence(delimiterTemp);
		  
		  String entry = inputParams[2];
		  String value = getValueCustom(name);
		  String[] values = value.split(delimiter);
		  int index = Integer.parseInt(entry);
		  if(index < values.length)
		  {
			  return values[index];
		  }
		  else
		  {
			  return "null";
		  }	  
	  }
	  catch(Exception e){
		  return "null";
	  }
  }
  public String escapeSequence(String delim)
  {
		String delimiterTemp = delim;
		String delimiter= "";
		  for(int i = 0; i< delimiterTemp.length(); i++)
		  {
			  delimiter = delimiter + "\\" + delimiterTemp.charAt(i) ;
		  }
		return delimiter;
  }
  public String toString()
  {
    StringBuffer buff = new StringBuffer();

    buff.append("mSession[").append(this.mSession).append("]\n");

    for (Enumeration e = elements(); e.hasMoreElements(); )
    {
      Cookie c = (Cookie)e.nextElement();
      buff.append("[").append(c.getName()).append("]=[");
      buff.append(c.getValue()).append("]\n");
    }

    return buff.toString();
  }

  public Element toElement()
  {
    return toElement("Cookies");
  }

  public Element toElement(String elementName)
  {
    Element cookies = DocumentHelper.createElement(elementName);

    cookies.addAttribute("Class", getClass().getName());

    if (null != this.mSession)
    {
      Element session = XmlEmittableUtils.toElement("Session", this.mSession);
      cookies.add(session.addAttribute("type", "session"));
    }

    for (Enumeration enu = elements(); enu.hasMoreElements(); )
    {
      Element cookie = XmlEmittableUtils.toElement("Cookie", (Cookie)enu.nextElement());
      cookies.add(cookie);
    }

    return cookies;
  }
}