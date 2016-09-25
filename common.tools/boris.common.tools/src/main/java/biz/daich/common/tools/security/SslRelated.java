/**
 *
 */
package biz.daich.common.tools.security;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Set of tools to work with SSL (HTTPS) and KeyStore
 *
 * @author Boris Daich
 */
public class SslRelated
{
    private static final Logger l = Logger.getLogger(SslRelated.class);

    /**
     *
     */
    private SslRelated()
    {}

    /**
     *
     */
    public static final String JSSECACERTS_FILE_NAME = "jssecacerts";
    /**
     *
     */
    public static final String CACERTS_FILE_NAME     = "cacerts";

    /**
     * Get File of the current JVM's (looks for it in the ${java.home}/lib/security folder ) jssecacerts or (cacerts in case former does not exists ) files.
     * <p>
     * Notes: None
     * <p>
     *
     * @return File of the keyStore
     * @throws FileNotFoundException
     *             if the path ${java.home}/lib/security does not have jssecacerts or cacerts files. As cacerts shiped by default with JDK/JRE there is something seriously wrong
     *             with the java installation
     * @author Boris Daich
     */
    public static File getCurrentJVMKeyStoreFile() throws FileNotFoundException
    {
        char SEP = File.separatorChar;
        File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
        File file = new File(dir, JSSECACERTS_FILE_NAME);
        if (file.isFile() == false)
        {
            file = new File(dir, CACERTS_FILE_NAME);
            if (!file.isFile() || !file.exists()) throw new FileNotFoundException("Could not find jssecacerts or cacerts file in " + dir.getAbsolutePath());
        }
        return file;
    }

    /**
     * Creates an empty KeyStore file and save it to the absoluteKeyStoreFilePath<br>
     * <b>Note:</b> if file exists it will be overwritten <br>
     *
     * @return KeyStore object created
     * @param absoluteKeyStoreFilePath
     *            path to store the new KeyStore. If exists will be overwritten without warning
     * @param keyStorePassword
     *            password to set for the KeyStore.
     * @throws KeyStoreException
     *             - if applicable
     * @throws NoSuchAlgorithmException
     *             - if applicable
     * @throws CertificateException
     *             - if applicable
     * @throws IOException
     *             - if applicable
     */
    public static KeyStore createNewEmptyKeyStoreFile(File absoluteKeyStoreFilePath, String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        return writeKeyStoreToFile(createNewKeyStore(keyStorePassword), absoluteKeyStoreFilePath, keyStorePassword);
    }

    // this is the same as current implementation that split to 2 more convenient functions
    //    public static KeyStore createNewEmptyKeyStoreFile(File absoluteKeyStoreFilePath, String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    //    {
    //
    //        BufferedOutputStream bfs = new BufferedOutputStream(new FileOutputStream(absoluteKeyStoreFilePath));
    //        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    //        ks.load((InputStream) null, keyStorePassword.toCharArray());
    //        ks.store(bfs, keyStorePassword.toCharArray());
    //        bfs.close();
    //        return ks;
    //    }

    /**
     * Creates an empty KeyStore file<br>
     * <b>Note:</b> in memory only need to be saved to file <br>
     *
     * @return KeyStore object created
     * @param keyStorePassword
     *            password to set for the KeyStore.
     * @throws KeyStoreException
     *             - if applicable
     * @throws NoSuchAlgorithmException
     *             - if applicable
     * @throws CertificateException
     *             - if applicable
     * @throws IOException
     *             - if applicable
     */
    public static KeyStore createNewKeyStore(final String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load((InputStream) null, keyStorePassword.toCharArray());
        return ks;
    }

    /**
     * writes a KeyStore to a file at the absoluteKeyStoreFilePath<br>
     * <b>Note:</b> if file exists it will be overwritten <br>
     *
     * @param ks
     *            the keystore must not be null
     * @return KeyStore object created
     * @param keyStoreFilePath
     *            path to store the new KeyStore. If exists will be overwritten without warning
     * @param keyStorePassword
     *            password to set for the KeyStore.
     * @throws KeyStoreException
     *             - if applicable
     * @throws NoSuchAlgorithmException
     *             - if applicable
     * @throws CertificateException
     *             - if applicable
     * @throws IOException
     *             - if applicable
     */
    public static KeyStore writeKeyStoreToFile(final KeyStore ks, final File keyStoreFilePath, final String keyStorePassword)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        Preconditions.checkArgument(ks != null);
        BufferedOutputStream bfs = new BufferedOutputStream(new FileOutputStream(keyStoreFilePath));
        ks.store(bfs, keyStorePassword.toCharArray());
        bfs.close();
        return ks;

    }

    /**
     * Retrives all X509Certificates of the Server at @host:port
     *
     * @param host
     *            - must be a valid hostname of the SSL protected server to connect. "localhost" is acceptable.
     * @param port
     *            - must be a valid port number to connect on the host. For example default https port is 443
     * @return certificates as provided by the server
     * @throws NoSuchAlgorithmException
     *             - if applicable
     * @throws KeyStoreException
     *             - if applicable
     * @throws KeyManagementException
     *             - if applicable
     * @throws UnknownHostException
     *             - if applicable
     * @throws IOException
     *             - if applicable
     * @throws CertificateEncodingException
     *             - if applicable
     */
    public static X509Certificate[] getCertificatesForHostPort(String host, int port)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnknownHostException, IOException, CertificateEncodingException
    {
        if (host == null || "".equals(host.trim())) throw new IllegalArgumentException("hostname can not be empty");
        if (port < 1) throw new IllegalArgumentException("invalid port number");

        final SSLContext context = SSLContext.getInstance("TLS");
        // this is the way to get a reference from the inner class
        final ThreadLocal<X509Certificate[]> certificateChain = new ThreadLocal<X509Certificate[]>();
        final TrustManager tm = new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers()
            {
                throw new UnsupportedOperationException();
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                throw new UnsupportedOperationException();
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                certificateChain.set(chain);
            }
        };

        context.init(null, new TrustManager[] { tm }, null);
        final SSLSocketFactory factory = context.getSocketFactory();
        l.trace("Opening connection to " + host + ":" + port + "...");
        final SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try (SSLSocket sslSocket = socket)
        // be sure that the socket closed
        {
            l.trace("Starting SSL handshake...");
            socket.setUseClientMode(true);
            socket.startHandshake();
            l.warn("Thats Strange No errors, certificate is already trusted how can it be?");
        }
        catch (SSLException e)
        {
            l.trace("As Expected we got an SSLException we aimed for. Well, we trust no one", e);
        }
        final X509Certificate[] chain = certificateChain.get();

        if (chain == null) { throw new IOException("Could not obtain server certificate chain"); }
        l.debug("Server sent " + chain.length + " certificate(s):");
        l.debug(prettyPrintCertificateChain(chain));
        return chain;
    }

    /**
     * convert the X509Certificate array to the human readable form like
     * <p>
     * <code>
     * [1]                                                         <br>
     *  Subject CN=localhost, O=WSO2, L=Mountain View, ST=CA, C=US         <br>
     *  Issuer CN=localhost, O=WSO2, L=Mountain View, ST=CA, C=US         <br>
     *  sha1 6b f8 e1 36 eb 36 d4 a5 6e a0 5c 7a e4 b9 a4 5b 63 bf 97 5d  <br>
     *  md5 02 fb aa 5f 20 64 49 4a 27 29 55 71 83 f7 46 cd               <br>
     * </code>
     * <p>
     * useful for debug purposes
     *
     * @param chain
     *            -the X509Certificate chain
     * @return human readable string
     * @throws NoSuchAlgorithmException
     *             - if applicable
     * @throws CertificateEncodingException
     *             - if applicable
     */
    public static StringBuilder prettyPrintCertificateChain(X509Certificate[] chain) throws NoSuchAlgorithmException, CertificateEncodingException
    {
        final StringBuilder sb = new StringBuilder();
        if (chain == null || chain.length == 0) return sb;

        final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++)
        {
            X509Certificate cert = chain[i];
            sb.append("\n[" + (i + 1) + "]\n");
            sb.append("Subject " + cert.getSubjectDN());
            sb.append("\n");
            sb.append(" Issuer " + cert.getIssuerDN());
            sb.append("\n");
            sha1.update(cert.getEncoded());
            sb.append(" sha1 " + toHexString(sha1.digest()));
            sb.append("\n");
            md5.update(cert.getEncoded());
            sb.append(" md5 " + toHexString(md5.digest()));
        }
        return sb;
    }

    /**
     * used by toHexString()
     */
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    /**
     * Convert array of byte to an string of HEX numbers like "6b f8 e1 36 eb 36 d4 a5 6e a0 5c"
     *
     * @param bytes
     *            to convert
     * @return converted string empty if bytes are null or empty
     */
    private static String toHexString(byte[] bytes)
    {
        if (bytes == null || bytes.length == 0) return "";

        final StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes)
        {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

}
