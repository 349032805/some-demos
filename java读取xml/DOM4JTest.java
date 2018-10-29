package com.loanmarket.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import loanmarket.domain.Book;


public class DOM4JTest {

	public static void main(String[] args) {
		ArrayList<Book> bookList = new ArrayList<Book>();

		// 1.创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			// 2.通过reader对象的read（）方法加载books.xml文件，获取document对象
			Document document = reader.read(new File("src/books.xml"));

			// 3.通过document对象获取根节点bookstore
			Element bookStore = document.getRootElement();

			// 4.通过element对象的elementIterator获取迭代器
			Iterator it = bookStore.elementIterator();

			// 5.遍历迭代器，获取根节点中的信息
			while (it.hasNext()) {
				System.out.println("==========开始某一本书遍历===============");

				Element book = (Element) it.next();
				Book bookData = new Book();

				// 6.获取book的属性名和属性值
				List<Attribute> bookAttrs = book.attributes();
				for (Attribute attr : bookAttrs) {
					System.out.println("属性名：" + attr.getName() + "--" + "属性值：" + attr.getValue());

					if (attr.getName().equals("id")) {
						bookData.setId(attr.getValue());
					}
				}

				// 7.通过book对象的elementIterator获取节点元素迭代器
				Iterator itt = book.elementIterator();

				// 8.遍历迭代器，获取子节点中的信息
				while (itt.hasNext()) {
					Element bookChild = (Element) itt.next();

					// 9.获取节点名和节点值
					// System.out.println("节点名：" + bookChild.getName()
					// + "--- 节点值： " + bookChild.getStringValue());
					System.out.println("节点名：" + bookChild.getName() + "--- 节点值： " + bookChild.getText());

					if (bookChild.getName().equals("name")) {
						bookData.setName(bookChild.getText());
					} else if (bookChild.getName().equals("author")) {
						bookData.setAuthor(bookChild.getText());
					} else if (bookChild.getName().equals("year")) {
						bookData.setYear(bookChild.getText());
					} else if (bookChild.getName().equals("price")) {
						bookData.setPrice(bookChild.getText());
					} else if (bookChild.getName().equals("language")) {
						bookData.setLanguage(bookChild.getText());
					}
				}
				// 遍历完一个节点，将该节点信息添加到列表中
				bookList.add(bookData);

				System.out.println("==========结束某一本书遍历===============");
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("输出XML在内存中的数据");
		System.out.println("保存的数据大小是" + bookList.size());

		// 输出保存在内存中XML信息
		for (Book book : bookList) {
			System.out.println("===输出开始====");
			System.out.println(book.getName());
			System.out.println("id=" + book.getId());
			System.out.println(book.getAuthor());
			System.out.println(book.getYear());
			System.out.println(book.getPrice());
			System.out.println(book.getLanguage());
			System.out.println("===输出结束===");
		}
	}

}
