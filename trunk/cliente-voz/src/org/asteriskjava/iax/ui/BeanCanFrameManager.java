// NAME
//      $RCSfile: BeanCanFrameManager.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision$
// CREATED
//      $Date$
// COPYRIGHT
//      Mexuar Technologies Ltd
// TO DO
//

package org.asteriskjava.iax.ui;

import java.net.*;

import java.awt.event.*;

import org.asteriskjava.iax.protocol.*;
import org.asteriskjava.iax.protocol.netse.*;
import org.asteriskjava.iax.audio.AudioInterface;

/**
 * Place to put code specific to the protocol - allowing BeanCanFrame to
 * keep the UI code only
 * @author <a href="mailto:thp@westhawk.co.uk">Tim Panton</a>
 * @version $Revision$ $Date$
 *
 */

public class BeanCanFrameManager
    implements ProtocolEventListener, CallManager {
    private static final String version_id =
        "@(#)$Id$ Copyright Mexuar Technologies Ltd";

    private Call _ca = null;
    private Friend _peer = null;
    private String _username = "";
    private String _password = "";
    private String _host = "";
    private String _num = "1004";
    private Binder _bind = null;
    private boolean _isApplet = false;
    private AudioInterface _audioBase = null;

    public BeanCanFrameManager(boolean isApplet, int level, String host) {
        super();
        Log.setLevel(level);
        _isApplet = isApplet;
        _host = host;
    }

    public void start() {
        //this.show();
        _audioBase = new org.asteriskjava.iax.audio.javasound.Audio8k();
        try {
            _bind = new BinderSE(_host, _audioBase);
        }
        catch (SocketException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

    public void stop() {
        if (_bind != null) {
            _bind.stop();
        }
        System.out.println("Stopped");
        _bind = null;
    }

    public BeanCanFrameManager(String username, String password, String host,
                               boolean isApplet, int level) {
        this(isApplet, level, host);
        _username = username;
        _password = password;
    }

    void register() {
        if (_bind == null) {
            start();
        }
        try {
            boolean reg = !("callmcom".equals(_password));
            _bind.register(_username, _password, this, reg);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * newCall
     *
     * @param c Call
     */
    public void newCall(Call c) {
        Log.debug("in newCall ");
        if (_ca == null) {
            _ca = c;
            Log.debug("_ca == null :" + _ca.getStatus());
            System.out.println(c.getStatus());
            if (_ca.getIsInbound()) {
                System.out.println("Answer");
            }
            else {
                System.out.println("Hangup");
            }
        }
        else {
            Log.debug("_ca != null :" + _ca.getStatus());
            System.out.println("Ignoring second call");
        }
    }

    /**
     * registered
     *
     * @param f Friend
     * @param s boolean
     */
    public void registered(Friend f, boolean s) {
        _peer = f;
        System.out.println(_peer.getStatus());
    }

    /**
     * hungUp
     *
     * @param c Call
     */
    public void hungUp(Call c) {
        _ca = null;
        System.out.println("idle");
        System.out.println("Call");
    }

    /**
     * ringing
     *
     * @param c Call
     */
    public void ringing(Call c) {
        System.out.println("Ringing");
    }

    /**
     * Lets us know that the call we made is answered (or
     * not).
     *
     * @param c Call
     * @see ProtocolEventListener#answered(Call)
     */
    public void answered(Call c) {
        System.out.println("Answered " + c.isAnswered());
    }

    /**
     * Called when it is known whether or not friend can reach its host
     * (PBX).
     *
     * @param f Friend
     * @param b Whether friend can reach its host
     * @param roundtrip The round trip (ms) of the request
     * @todo implement
     */
    public void setHostReachable(Friend f, boolean b, int roundtrip) {
        Log.warn("setHostReachable " + b + ", roundtrip " + roundtrip);
    }

    /**
     */
    void dialString_actionPerformed(ActionEvent e) {
        if (_ca == null) {
            if (_peer != null) {
                _peer.newCall(_username, _password, _num, null, null);
            }
        }
        else {
            if (_ca.getIsInbound()) {
                if (_ca.isAnswered()) {
                    _ca.hangup();
                }
                else {
                    _ca.answer();
                    System.out.println("Desligar");
                }
            }
            else {
                _ca.hangup();
            }
        }
    }

    
    public String get_host() {
        return _host;
    }

    public String get_password() {
        return _password;
    }

    public String get_username() {
        return _username;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public void set_host(String _host) {
        this._host = _host;
    }

    public boolean accept(Call ca) {
        Log.debug("in accept ");
        boolean ret = true;
        if (_ca != null) {
            ret = false;
        }
        return ret;
    }

    boolean canRecord() {
        boolean ret = false;
        javax.sound.sampled.AudioPermission ap = new javax.sound.sampled.
            AudioPermission("record");
        try {
            java.security.AccessController.checkPermission(ap);
            ret = true;
            Log.debug("Have permission to access microphone");
        }
        catch (java.security.AccessControlException ace) {
            Log.debug("Do not have permission to access microphone");
            Log.warn(ace.getMessage());
        }
        return ret;
    }

    /**
     * set_debug
     *
     * @param debug int
     */
    public void set_debug(int debug) {
        Log.setLevel(debug);
    }

}

