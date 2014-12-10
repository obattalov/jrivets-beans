package org.jrivets.beans.auth;

import org.jrivets.kvstorage.KeyValueStorage;
import org.jrivets.util.UID;

public interface SessionsStorage extends KeyValueStorage<UID, Session> {

}
