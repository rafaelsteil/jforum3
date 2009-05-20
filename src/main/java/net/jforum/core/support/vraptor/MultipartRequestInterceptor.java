package net.jforum.core.support.vraptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.vraptor.Interceptor;
import org.vraptor.LogicException;
import org.vraptor.LogicFlow;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.interceptor.BasicUploadedFileInformation;
import org.vraptor.interceptor.UploadedFileInformation;
import org.vraptor.view.ViewException;

/**
 * Interceptor capable of parsing the input stream.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 */
public class MultipartRequestInterceptor implements Interceptor {
	private static final Logger LOG = Logger.getLogger(MultipartRequestInterceptor.class);
	private final File temporaryDirectory;

	public MultipartRequestInterceptor() throws IOException {
		temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
	}

	@SuppressWarnings( { "unchecked", "deprecation" })
	public void intercept(LogicFlow flow) throws LogicException, ViewException {
		if (!FileUploadBase.isMultipartContent(flow.getLogicRequest().getRequest())) {
			flow.execute();
			return;
		}

		VRaptorServletRequest servletRequest = flow.getLogicRequest().getRequest();

		LOG.debug("Trying to parse multipart request.");

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory(4096 * 16, temporaryDirectory);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Using repository [" + factory.getRepository() + "]");
		}

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		List<FileItem> fileItems;

		// assume we know there are two files. The first file is a small
		// text file, the second is unknown and is written to a file on
		// the server
		try {
			fileItems = upload.parseRequest(servletRequest);
		}
		catch (FileUploadException e) {
			LOG.warn("There was some problem parsing this multipart request, or someone is not sending a "
				+ "RFC1867 compatible multipart request.", e);
			flow.execute();
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Found [" + fileItems.size() + "] attributes in the multipart form submission. Parsing them.");
		}

		for (FileItem item : fileItems) {
			if (item.isFormField()) {
				servletRequest.addParameterValue(item.getFieldName(), item.getString());
			}
			else {
				if (!item.getName().trim().equals("")) {
					try {
						File file = File.createTempFile("raptor.", ".upload");
						file.deleteOnExit();

						item.write(file);

						UploadedFileInformation fileInformation = new BasicUploadedFileInformation(
							file, item.getName(), item.getContentType());

						this.registeUploadedFile(servletRequest, item.getFieldName(), fileInformation);

						LOG.info("Uploaded file: " + item.getFieldName() + " with " + fileInformation);
					}
					catch (Exception e) {
						LOG.error("Nasty uploaded file " + item.getName(), e);
					}
				}
				else {
					LOG.info("A file field was empy: " + item.getFieldName());
				}
			}
		}

		flow.execute();
	}

	private void registeUploadedFile(VRaptorServletRequest request, String name, Object value) {
		if (request.getAttribute(name) == null) {
			request.setAttribute(name, value);
		}
		else {
			Object currentValue = request.getAttribute(name);

			if (!currentValue.getClass().isArray()) {
				request.setAttribute(name, new Object[] { currentValue, value });
			}
			else {
				Object[] currentArray = (Object[]) currentValue;
				Object[] newArray = new Object[currentArray.length + 1];

				for (int i = 0; i < currentArray.length; i++) {
					newArray[i] = currentArray[i];
				}

				newArray[currentArray.length] = value;
				request.setAttribute(name, newArray);
			}
		}
	}
}
