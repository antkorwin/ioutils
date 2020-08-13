package com.antkorwin.ioutils.multipartfile;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import com.antkorwin.throwable.functions.ThrowableWrapper;
import org.apache.commons.io.IOUtils;

import org.springframework.http.HttpHeaders;


/**
 * Утиль для формирования HTTP ответа сервера
 * с вложенным бинарным файлом
 *
 * @author Korovin Anatoliy
 */
public class FileResponseHelper {

	/**
	 * Формирует HTTP ответ с бинарным фалйом и выставляет заголовки
	 * для имения файла и типа вложения
	 *
	 * @param contentStreamSupplier контент для отправки
	 * @param mimeType              тип контента
	 * @param contentDisposition    header Content-Disposition
	 * @param response              HttpServletResponse в который будет скопирован стрим с данными
	 */
	public static void makeResponseWithFile(ThrowableSupplier<InputStream> contentStreamSupplier,
	                                        String mimeType,
	                                        String contentDisposition,
	                                        HttpServletResponse response) {
		ThrowableWrapper.run(() -> {
			internalMakeResponseWithFile(contentStreamSupplier,
			                             mimeType,
			                             contentDisposition,
			                             response);
		});
	}

	private static void internalMakeResponseWithFile(ThrowableSupplier<InputStream> contentStreamSupplier,
	                                                 String mimeType,
	                                                 String contentDisposition,
	                                                 HttpServletResponse response) throws IOException {

		try (InputStream inputStreamContent = contentStreamSupplier.get()) {
			response.setContentType(mimeType);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
			IOUtils.copy(inputStreamContent, response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
