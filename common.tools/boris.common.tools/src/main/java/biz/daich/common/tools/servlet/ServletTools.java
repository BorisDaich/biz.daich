package biz.daich.common.tools.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Utilities for Servlet environment
 *
 * @author Boris Daich
 */
public class ServletTools
{

    /**
     * @param uri
     *            one to check
     * @return one with the slash
     */
    public static String enshureUriEndsWithSlash(String uri)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(uri));
        if (!uri.endsWith("/")) uri = uri + "/";
        return uri;
    }

    /**
     * @param request
     *            - the request
     * @return string of the form "protocol://hostname:port" - NOTE: no slash at the end
     * @throws MalformedURLException
     *             - should not happen
     */
    public static String getProtocolHostPort(HttpServletRequest request) throws MalformedURLException
    {
        final URL requestUrl = new URL(request.getRequestURL().toString());
        final String protocolHostPort = requestUrl.getProtocol() + "://" + requestUrl.getHost() + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort());
        return protocolHostPort;
    }

}
