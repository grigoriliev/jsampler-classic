module com.grigoriliev.jsampler.classic {
	requires com.grigoriliev.jsampler;
	requires com.grigoriliev.jsampler.jlscp;
	requires com.grigoriliev.jsampler.juife;
	requires com.grigoriliev.jsampler.juife.swing;
	requires com.grigoriliev.jsampler.swing;

	requires java.desktop;
	requires java.logging;
	requires java.prefs;

	exports com.grigoriliev.jsampler.classic.view;
}