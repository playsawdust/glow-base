package com.playsawdust.glow.image.io;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.LinearImageData;
import com.playsawdust.glow.image.io.tiff.BaselineTiffHeader;
import com.playsawdust.glow.image.io.tiff.IFD;
import com.playsawdust.glow.image.io.tiff.TiffImageDataDecoder;
import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;
import com.playsawdust.glow.vecmath.Vector2d;

public class TiffImageIO {
	private static final int BIG_ENDIAN    = 0x4D4D; // 'MM' / Motorola 680x0 ordering
	private static final int LITTLE_ENDIAN = 0x4949; // 'II' / Intel 80x86 ordering
	
	public Vector2d getSize(DataSlice in) throws IOException {
		in.seek(0L);
		List<IFD> ifds = loadIFDs(in);
		
		IFD mainImage = ifds.get(0);
		long width = mainImage.getLong(in, IFD.TAG_IMAGE_WIDTH, 0L);
		long height = mainImage.getLong(in, IFD.TAG_IMAGE_HEIGHT, 0L);
		return new Vector2d(width, height);
	}
	
	public static LinearImageData load(DataSlice in) throws IOException {
		in.seek(0L);
		List<IFD> ifds = loadIFDs(in);
		
		IFD mainImage = ifds.get(0);
		
		long width = mainImage.getLong(in, IFD.TAG_IMAGE_WIDTH, 0L);
		long height = mainImage.getLong(in, IFD.TAG_IMAGE_HEIGHT, 0L);
		
		long linearSize = width * height;
		if (width > Integer.MAX_VALUE || height > Integer.MAX_VALUE) throw new IOException("Image is too big to load into memory!");
		if (linearSize < 0 || linearSize > Integer.MAX_VALUE) throw new IOException("Image is too big to load into memory!");
		
		LinearImageData result = new LinearImageData((int) width, (int) height);
		
		BaselineTiffHeader header = new BaselineTiffHeader(mainImage, in);
		
		for(int y=0; y<height; y++) {
			TiffImageDataDecoder.getLineWindow(in, header, 0, y, header.width, result, 0, y);
		}
		
		return result;
	}
	
	public static LinearImageData loadSubImage(DataSlice in, long srcX, long srcY, long srcWidth, long srcHeight) throws IOException {
		in.seek(0L);
		List<IFD> ifds = loadIFDs(in);
		
		IFD mainImage = ifds.get(0);
		
		long width = mainImage.getLong(in, IFD.TAG_IMAGE_WIDTH, 0L);
		long height = mainImage.getLong(in, IFD.TAG_IMAGE_HEIGHT, 0L);
		
		long linearSize = srcWidth * srcHeight;
		if (width > Integer.MAX_VALUE || height > Integer.MAX_VALUE) throw new IOException("SubImage is too big to load into memory!");
		if (linearSize < 0 || linearSize > Integer.MAX_VALUE) throw new IOException("SubImage is too big to load into memory!");
		
		LinearImageData result = new LinearImageData((int) srcWidth, (int) srcHeight);
		
		//Extract pixel format from this image's IFD
		BaselineTiffHeader header = new BaselineTiffHeader(mainImage, in);
		
		for(int y=0; y<srcHeight; y++) {
			TiffImageDataDecoder.getLineWindow(in, header, srcX, srcY + y, srcWidth, result, 0, y);
		}
		
		return result;
	}
	
	public static List<IFD> loadIFDs(DataSlice in) throws IOException {
		int byteOrder = in.readI16u();
		if (byteOrder == BIG_ENDIAN) {
			in.setByteOrder(ByteOrder.BIG_ENDIAN);
		} else if (byteOrder == LITTLE_ENDIAN) {
			in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		} else {
			throw new IOException("Unrecognized byte order '"+(char) ((byteOrder>>8) & 0xFF)+(char) (byteOrder & 0xFF) +"'.");
		}
		
		int fortyTwo = in.readI16u();
		if (fortyTwo == 43) {
			return loadBigIFDs(in);
		}
		if (fortyTwo != 42) throw new IOException("Byte order validity check failed. Expected 42, found "+fortyTwo);
		
		long firstIFD = (long) in.readI32s() & 0xFFFFFFFFL;
		
		in.seek(firstIFD);
		
		ArrayList<IFD> ifds = new ArrayList<>();
		IFD ifd = IFD.read(in);
		ifds.add(ifd);
		
		while(ifd.nextIFD != 0L) {
			in.seek(ifd.nextIFD);
			ifd = IFD.read(in);
			ifds.add(ifd);
		}
		
		return ifds;
	}
	
	public static List<IFD> loadBigIFDs(DataSlice in) throws IOException {
		//System.out.println("Loading as BigTIFF");
		int pointerSize = in.readI16u();
		if (pointerSize > 8) throw new IOException("BigTIFF pointer size of "+pointerSize+"!!! No contemporary program can read this!");
		int reserved = in.readI16u();
		if (reserved != 0) throw new IOException("Data reserved for future versions is set. Reading further will yield corrupted data.");
		
		long firstIFD = in.readI64s();
		in.seek(firstIFD);
		//System.out.println("Seeked to IFD at "+in.position());
		
		ArrayList<IFD> ifds = new ArrayList<>();
		IFD ifd = IFD.readBig(in);
		ifds.add(ifd);
		
		while(ifd.nextIFD != 0L) {
			in.seek(ifd.nextIFD);
			ifd = IFD.readBig(in);
			ifds.add(ifd);
		}
		
		return ifds;
	}
	
	public static DataSlice saveToDataSlice(ImageData image) throws IOException {
		DataBuilder out = DataBuilder.create();
		
		out.writeI16u(BIG_ENDIAN);
		out.writeI16u(42);
		
		//TODO: Write image data
		
		//TODO: Write IFD
		
		return out.toDataSlice();
	}
}
