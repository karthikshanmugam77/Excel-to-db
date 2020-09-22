package com.pdfcreator.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pdfcreator.creator.CreatorClass;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {

	static int i = 0;
	String[] name = new String[20];
	String[] password = new String[20];
	String[] mail = new String[20];

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Inside doget -> "+i);
		
		int Entry =Integer.parseInt(request.getParameter("entry"));
		
		if (i < Entry) {
			String t1 = request.getParameter("mail");
			String t2 = request.getParameter("name");
			String t3 = request.getParameter("password");
			name[i] = t1;
			mail[i] = t2;
			password[i] = t3;
			
			
			for(int temp =0;temp<=i;temp++)
			{
				System.out.println( " name -> "+ name[temp]+ " / mail -> "+ mail[temp]+" / password -> "+ password[temp]);
			}
			i++;
			
		}
		if (i >=Entry) {
			
			System.out.println("        I > 2 -- create function..");
			final ServletContext servletContext = request.getSession().getServletContext();
			final File tempDirectory = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			final String temperotyFilePath = tempDirectory.getAbsolutePath();

			String fileName = "Generate_Report_" + LocalDate.now() + ".pdf";
			response.setContentType("application/pdf");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-disposition", "attachment; " + "filename=" + fileName);
//			String mail = request.getParameter("mail");
//			String name = request.getParameter("name");
//			String password = request.getParameter("password");

			try {
				System.out.println("  inside try ....");

				CreatorClass.createPDF(temperotyFilePath + "\\" + fileName, mail, name, password,Entry);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos = convertPDFToByteArrayOutputStream(temperotyFilePath + "\\" + fileName);
				OutputStream os = response.getOutputStream();
				baos.writeTo(os);
				os.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	private static ByteArrayOutputStream convertPDFToByteArrayOutputStream(String fileName) {

		InputStream inputStream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			inputStream = new FileInputStream(fileName);

			byte[] buffer = new byte[1024];
			baos = new ByteArrayOutputStream();

			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baos;
	}

}
