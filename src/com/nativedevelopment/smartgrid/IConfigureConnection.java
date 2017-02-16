package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IConfigureConnection extends Serializable {
	public UUID GetTypeIdentifier();
	public ISetting[] GetSettings();
}
