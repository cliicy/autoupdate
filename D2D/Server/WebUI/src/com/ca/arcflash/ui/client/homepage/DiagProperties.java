package com.ca.arcflash.ui.client.homepage;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DiagProperties extends PropertyAccess<DiagLogModel> {
    ValueProvider<DiagLogModel, String> logCollectionDesciptionText();
    ModelKeyProvider<DiagLogModel> id();
  }
