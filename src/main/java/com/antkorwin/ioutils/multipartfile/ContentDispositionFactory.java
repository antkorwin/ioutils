package com.antkorwin.ioutils.multipartfile;

import java.net.URLEncoder;

import com.antkorwin.throwable.functions.ThrowableWrapper;


/**
 * Created on 21/05/2020
 * <p>
 * Используется для построения значения в header ContentDisposition
 *
 * @author Korovin Anatoliy
 */
public class ContentDispositionFactory {

	/**
	 * Формирует ContentDisposition по имения файла
	 * при этом конвертирует имя файла в формат application/x-www-form-urlencoded
	 *
	 * @param filename имя файла
	 * @return построенный ContentDisposition
	 */
	public static String getWithUtf8Filename(String filename) {
		return ThrowableWrapper.get(() -> {
			String urlEncodedFileName = URLEncoder.encode(filename, "UTF-8")
			                                      //Костыль для url энкодера, чтобы при скачивании
			                                      //все символы определялись, как положено
			                                      .replaceAll("\\+", "%20");
			return "attachment;filename*=UTF-8''${urlEncodedFileName}";
		});
	}

	/**
	 * Заменяет все символы не входящие в ASCII, в имени фала на символ _
	 * и формирует ContentDisposition
	 *
	 * @param filename исходное имя файла
	 * @return сформированный ContentDisposition
	 */
	public static String getWithAsciiFilename(String filename) {
		String escapedname = makeSafeFileName(filename);
		return "attachment;filename=\"${escapedname}\"";
	}


	private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._-~()[]{}0123456789";

	private static String makeSafeFileName(String fileName) {

		char[] newFileName = fileName.toCharArray();
		for (int i = 0; i < newFileName.length; i++) {
			if (ALLOWED_CHARS.indexOf(newFileName[i]) == -1) {
				newFileName[i] = '_';
			}
		}
		return new String(newFileName);
	}
}
