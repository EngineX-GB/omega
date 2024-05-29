package com.enginex.service;

import com.enginex.model.AuditResponseCode;
import com.enginex.model.Link;

public interface AuditService {

    AuditResponseCode isDuplicate(Link link);

    AuditResponseCode update(Link link);

}
