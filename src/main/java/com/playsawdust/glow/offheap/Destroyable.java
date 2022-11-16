package com.playsawdust.glow.offheap;

/**
 * Destroyables manage something offheap. It could be non-heap memory, GPU memory, audio devices, or other hardware
 * features, but the garbage collector can't help us here. Creating objects that implement Destroyable or a subclass
 * imply the creation of these offheap resources, and these objects will need {@link #destroy()} to be explicitly called
 * on them before they are garbage collected. Once destroy has been called, further usage of these objects is forbidden.
 * 
 * <p>Previous versions of this concept implemented AutoCloseable. Unfortunately, this triggers some pretty ubiquitous
 * IDE warnings, and generally Destroyables are very long-lived, unlike their stack and stream cousins. Therefore the
 * gains of AutoCloseable are minimal.
 */
public interface Destroyable {
	/**
	 * Frees up any offheap resources managed by this object. After calling this method, this object should be
	 * considered invalid and no further calls may be made to its methods.
	 */
	void destroy();
}
