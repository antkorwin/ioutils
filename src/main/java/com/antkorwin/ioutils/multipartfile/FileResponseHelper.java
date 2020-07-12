package com.antkorwin.ioutils.multipartfile;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

import com.antkorwin.ioutils.error.InternalException;
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
	 * @param content            контент для отправки
	 * @param mimeType           тип контента
	 * @param contentDisposition header Content-Disposition
	 * @param response           HttpServletResponse в который будет скопирован стрим с данными
	 */
	public static void makeResponseWithFile(InputStream content,
	                                        String mimeType,
	                                        String contentDisposition,
	                                        HttpServletResponse response) {
		ThrowableWrapper.run(() -> {
			internalMakeResponseWithFile(content,
			                             mimeType,
			                             contentDisposition,
			                             response);
		});
	}

	private static void internalMakeResponseWithFile(InputStream content,
	                                                 String mimeType,
	                                                 String contentDisposition,
	                                                 HttpServletResponse response) throws IOException {
		if (content == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			throw new InternalException("Content must be not null in `internalMakeResponseWithFile(null,${mimeType},${contentDisposition},${response})");
		}

		response.setContentType(mimeType);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
		IOUtils.copy(content, response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
