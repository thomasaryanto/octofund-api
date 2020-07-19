package com.thomasariyanto.octofund.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.MutualFundPackageRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.dao.TransactionRepo;
import com.thomasariyanto.octofund.dao.TransactionStatusRepo;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.MutualFundPackage;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.Transaction;
import com.thomasariyanto.octofund.service.TransactionService;
import com.thomasariyanto.octofund.util.EmailUtil;
import com.thomasariyanto.octofund.util.UploadUtil;

import net.bytebuddy.utility.RandomString;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	TransactionRepo transactionRepo;
	
	@Autowired
	TransactionStatusRepo transactionStatusRepo;
	
	@Autowired
	MemberRepo memberRepo;
	
	@Autowired 
	PortfolioRepo portfolioRepo;
	
	@Autowired
	MutualFundRepo mutualFundRepo;
	
	@Autowired
	MutualFundPackageRepo mutualFundPackageRepo;
	
	@Autowired
	private EmailUtil emailUtil;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@Override
	public Iterable<Transaction> getTransactions() {
		return transactionRepo.findAll();
	}
	
	@Override
	public Transaction getTransactionById(int id) {
		return transactionRepo.findById(id).get();
	}
	
	@Override
	public Page<Transaction> getTransactionByMemberId(int type, int memberId, int pageNo, int pageSize, String sortKey, String sortType) {
		Sort sort = sortType.equalsIgnoreCase("asc") ? Sort.by(sortKey).ascending() : Sort.by(sortKey).descending();
		Pageable page = PageRequest.of(pageNo, pageSize, sort);
		Page<Transaction> pagedResult = transactionRepo.findAllByTypeAndMemberId(type, memberId, page);
		
		return pagedResult;
	}
	
	@Override
	public Page<Transaction> getTransactionByManagerId(int type, int mutualFundId, int pageNo, int pageSize, String sortKey, String sortType) {
		Sort sort = sortType.equalsIgnoreCase("asc") ? Sort.by(sortKey).ascending() : Sort.by(sortKey).descending();
		Pageable page = PageRequest.of(pageNo, pageSize, sort);
		Page<Transaction> pagedResult = transactionRepo.findAllByTypeAndMutualFundId(type, mutualFundId, page);
		
		return pagedResult;
	}
	
	@Override
	public Page<Transaction> getVerifyTransactionByManagerId(int managerId, Pageable pageable) {
		return transactionRepo.findAllByTransactionStatusIdAndMutualFundManagerId(2, managerId, pageable);
	}
	
	@Override
	public Transaction addBuyTransaction(Transaction transaction) {
		MutualFund findMutualFund = mutualFundRepo.findById(transaction.getMutualFund().getId()).get();
		
		if(transaction.getTotalPrice() < findMutualFund.getMinimumBuy()) {
			throw new RuntimeException("Jumlah pembelian kurang dari minimum!");
		}
		
		if(findMutualFund.isLimited()) {
			if(findMutualFund.getStock() < transaction.getTotalPrice() / findMutualFund.getLastPrice()) {
				throw new RuntimeException("Stok reksadana ini tidak mencukupi!");
			}
		}
		
		transaction.setId(0);
		transaction.setType(1);
		transaction.setTransactionStatus(transactionStatusRepo.findById(1).get());
		transaction.setDate(new Date());
		return transactionRepo.save(transaction);
	}
	
	@Override
	@Transactional
	public String addBuyPackageTransaction(MultipartFile file, String transactionString, int packageId) throws JsonMappingException, JsonProcessingException{
		Transaction transaction = new ObjectMapper().readValue(transactionString, Transaction.class);
		MutualFundPackage findMutualFundPackage = mutualFundPackageRepo.findById(packageId).get();
		
		String paymentProofPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\payment\\";
		String paymentProof = fileUploader.uploadFile(file, paymentProofPath);
		String paymentProofUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("transactions/paymentproof/").path(paymentProof).toUriString();

		//jumlah pembelian
		double total1 = ((double)findMutualFundPackage.getPercentageOne() / 100) * transaction.getTotalPrice();
		double total2 = ((double)findMutualFundPackage.getPercentageTwo() / 100) * transaction.getTotalPrice();
		double total3 = ((double)findMutualFundPackage.getPercentageThree() / 100) * transaction.getTotalPrice();
		long totalPrice1 = (long) total1;
		long totalPrice2 = (long) total2;
		long totalPrice3 = (long) total3;
		System.out.println(totalPrice1);
		System.out.println(totalPrice2);
		System.out.println(totalPrice3);
		if(totalPrice1 < findMutualFundPackage.getProductOne().getMinimumBuy() || totalPrice2 < findMutualFundPackage.getProductTwo().getMinimumBuy() || totalPrice3 < findMutualFundPackage.getProductThree().getMinimumBuy()) {
			throw new RuntimeException("Jumlah pembelian kurang dari minimum!");
		}
		
		if(findMutualFundPackage.getProductOne().isLimited()) {
			if(findMutualFundPackage.getProductOne().getStock() < totalPrice1 / findMutualFundPackage.getProductOne().getLastPrice()) {
				throw new RuntimeException("Stok beberapa reksadana dalam paket ini tidak mencukupi!");
			}
		}
		
		if(findMutualFundPackage.getProductTwo().isLimited()) {
			if(findMutualFundPackage.getProductTwo().getStock() < totalPrice2 / findMutualFundPackage.getProductTwo().getLastPrice()) {
				throw new RuntimeException("Stok beberapa reksadana dalam paket ini tidak mencukupi!");
			}
		}
		
		if(findMutualFundPackage.getProductThree().isLimited()) {
			if(findMutualFundPackage.getProductThree().getStock() < totalPrice3 / findMutualFundPackage.getProductThree().getLastPrice()) {
				throw new RuntimeException("Stok beberapa reksadana dalam paket ini tidak mencukupi!");
			}
		}
		
		//proses
		Transaction transaction1 = new Transaction();
		transaction1.setId(0);
		transaction1.setMember(transaction.getMember());
		transaction1.setProductName(findMutualFundPackage.getProductOne().getName());
		transaction1.setManagerName(findMutualFundPackage.getProductOne().getManager().getCompanyName());
		transaction1.setBankName(transaction.getBankName());
		transaction1.setTotalPrice(totalPrice1);
		transaction1.setMutualFund(findMutualFundPackage.getProductOne());
		transaction1.setType(1);
		transaction1.setPaymentProof(paymentProofUri);
		transaction1.setTransactionStatus(transactionStatusRepo.findById(2).get());
		transaction1.setDate(new Date());
		transactionRepo.save(transaction1);
		
		Transaction transaction2 = new Transaction();
		transaction2.setId(0);
		transaction2.setMember(transaction.getMember());
		transaction2.setProductName(findMutualFundPackage.getProductTwo().getName());
		transaction2.setManagerName(findMutualFundPackage.getProductTwo().getManager().getCompanyName());
		transaction2.setBankName(transaction.getBankName());
		transaction2.setTotalPrice(totalPrice2);
		transaction2.setMutualFund(findMutualFundPackage.getProductTwo());
		transaction2.setType(1);
		transaction2.setPaymentProof(paymentProofUri);
		transaction2.setTransactionStatus(transactionStatusRepo.findById(2).get());
		transaction2.setDate(new Date());
		transactionRepo.save(transaction2);
		
		Transaction transaction3 = new Transaction();
		transaction3.setId(0);
		transaction3.setMember(transaction.getMember());
		transaction3.setProductName(findMutualFundPackage.getProductThree().getName());
		transaction3.setManagerName(findMutualFundPackage.getProductThree().getManager().getCompanyName());
		transaction3.setBankName(transaction.getBankName());
		transaction3.setTotalPrice(totalPrice3);
		transaction3.setMutualFund(findMutualFundPackage.getProductThree());
		transaction3.setType(1);
		transaction3.setPaymentProof(paymentProofUri);
		transaction3.setTransactionStatus(transactionStatusRepo.findById(2).get());
		transaction3.setDate(new Date());
		transactionRepo.save(transaction3);
		
		return "Pembelian paket berhasil!";
	}
	
	@Override
	public Transaction paymentBuyTransaction(int transactionId) {
		return transactionRepo.findByTypeAndId(1, transactionId);
	}
	
	@Override
	public Transaction confirmBuyTransaction(MultipartFile file, int transactionId) {
		String paymentProofPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\payment\\";
		String paymentProof = fileUploader.uploadFile(file, paymentProofPath);
		String paymentProofUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("transactions/paymentproof/").path(paymentProof).toUriString();
		
		Transaction findTransaction = transactionRepo.findById(transactionId).get();
		findTransaction.setTransactionStatus(transactionStatusRepo.findById(2).get());
		findTransaction.setRejectMessage(null);
		findTransaction.setPaymentProof(paymentProofUri);
		return transactionRepo.save(findTransaction);
	}
	
	@Override
	public ResponseEntity<Object> getProof(String fileName){
		String paymentProofPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\payment\\";
		Path path = Paths.get(paymentProofPath + fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
	
	@Override
	public String rejectBuyTransaction(Transaction transaction) {
		Transaction findTransaction = transactionRepo.findById(transaction.getId()).get();
		Member findMember = memberRepo.findById(findTransaction.getMember().getId()).get();

		findTransaction.setTransactionStatus(transactionStatusRepo.findById(3).get());
		findTransaction.setRejectMessage(transaction.getRejectMessage());
		transactionRepo.save(findTransaction);
		this.emailUtil.sendEmail(findMember.getUser().getEmail(), "Transaksi Pembelian Reksadana Gagal", "Pembayaran kamu untuk pembelian reksadana "+findTransaction.getMutualFund().getName()+" sebesar Rp. "+findTransaction.getTotalPrice()+" ditolak dikarenakan: "+transaction.getRejectMessage()+".");
		return "Transaksi berhasil ditolak!";
	}
	
	@Override
	public String acceptBuyTransaction(Transaction transaction) throws IOException {
		Transaction findTransaction = transactionRepo.findById(transaction.getId()).get();
		Member findMember = memberRepo.findById(findTransaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(findTransaction.getMutualFund().getId()).get();
		Optional<Portfolio> checkPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(findTransaction.getMember().getId(), findTransaction.getMutualFund().getId());
		
		double totalUnit = findTransaction.getTotalPrice() / findMutualFund.getLastPrice();
		
		if(findMutualFund.isLimited()) {
			if(findMutualFund.getStock() < totalUnit) {
				throw new RuntimeException("Stok reksadana tidak mencukupi, silahkan tolak transaksi!");
			}
		}
		
		if(checkPortfolio.toString() == "Optional.empty") {
			Portfolio portfolio = new Portfolio();
			portfolio.setMember(findMember);
			portfolio.setMutualFund(findMutualFund);
			portfolio.setTotalInvest(findTransaction.getTotalPrice());
			portfolio.setTotalUnit(totalUnit);
			portfolioRepo.save(portfolio);
		}
		else {
			Portfolio portfolio = checkPortfolio.get();
			portfolio.setTotalInvest(portfolio.getTotalInvest() + findTransaction.getTotalPrice());
			portfolio.setTotalUnit(portfolio.getTotalUnit() + totalUnit);
			portfolioRepo.save(portfolio);
		}
		
		if(findMutualFund.isLimited()) {
			findMutualFund.setStock(findMutualFund.getStock() - totalUnit);
			mutualFundRepo.save(findMutualFund);
		}
		
		findTransaction.setTransactionStatus(transactionStatusRepo.findById(4).get());
		findTransaction.setTotalUnit(totalUnit);
		transactionRepo.save(findTransaction);
		
		String pdfPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\invoice\\";
		String signaturePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\signature\\";
		String fileName = RandomString.make(20) +".pdf";
		String sigantureName = findMember.getSignature().replace("http://localhost:8080/users/image/signature/","");
		String invoiceUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("transactions/invoice/").path(fileName).toUriString();
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
		String strDate = dateFormat.format(date);  
		
		File file = new File(pdfPath + fileName);
		if (!file.exists()) {
		    file.createNewFile();
		}
         
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath + fileName));
			document.open();
			Font titleFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
			Font normalFont = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
			Paragraph title = new Paragraph("Surat Keterangan Transaksi OctoFund", titleFont);
			Paragraph content = new Paragraph("Pada tanggal "+strDate+" telah terjadi transaksi pembelian sebesar Rp."+findTransaction.getTotalPrice()+" untuk reksadana "+findTransaction.getProductName()+" dengan nomor transaksi #"+findTransaction.getId()+". Unit reksadana sebesar "+String.format("%.2f", totalUnit)+" unit dengan harga satuan Rp."+String.format("%.2f", findMutualFund.getLastPrice())+" / unit telah berhasil di kreditkan ke akun nasabah.", normalFont);
			Paragraph name = new Paragraph("("+findMember.getUser().getName()+")", normalFont);
			Image signature = Image.getInstance(signaturePath + sigantureName);
			
			title.setAlignment(Element.ALIGN_CENTER);
			content.setAlignment(Element.ALIGN_JUSTIFIED);
			name.setAlignment(Element.ALIGN_RIGHT);
			signature.setAlignment(Element.ALIGN_RIGHT);
			signature.scalePercent(50);
			document.add(title);
			document.add(Chunk.NEWLINE);
			document.add(content);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(signature);
			document.add(name);
			document.close();
			writer.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		findTransaction.setInvoice(invoiceUri);;
		transactionRepo.save(findTransaction);
		
		this.emailUtil.sendEmail(findMember.getUser().getEmail(), "Transaksi Pembelian Reksadana Berhasil", "Transaksi pembelian reksadana "+findTransaction.getMutualFund().getName()+" sebesar Rp. "+findTransaction.getTotalPrice()+" telah berhasil!\n\nSurat keterangan transaksi sudah diterbitkan dan dapat diunduh di: "+invoiceUri);
		

		return "Transaksi berhasil dikonfirmasi!";
	}
	
	@Override
	public ResponseEntity<Object> downloadFile(String fileName){
		String invoicePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\pdf\\invoice\\";
		Path path = Paths.get(invoicePath + fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
	
	@Override
	public String addSellTransaction(Transaction transaction) {
		Member findMember = memberRepo.findById(transaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(transaction.getMutualFund().getId()).get();
		Portfolio findPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(transaction.getMember().getId(), transaction.getMutualFund().getId()).get();
		
		
		double totalUnit = transaction.getTotalPrice() / findMutualFund.getLastPrice();
		 
		long balance = (long) (findPortfolio.getTotalUnit() * findMutualFund.getLastPrice());
		System.out.println(balance);
		
		if(transaction.getTotalPrice() > balance) {
			throw new RuntimeException("Jumlah penjualan tidak mencukupi!");
		}
		else if(transaction.getTotalPrice() == balance) {
			transaction.setTotalUnit(findPortfolio.getTotalUnit());
			portfolioRepo.delete(findPortfolio);
		}
		else {
			if (findPortfolio.getTotalInvest() - (totalUnit * findMutualFund.getLastPrice()) < 0) {
				findPortfolio.setTotalInvest(0);
			}
			else {
				findPortfolio.setTotalInvest(findPortfolio.getTotalInvest() - (long)(totalUnit * findMutualFund.getLastPrice()));
			}
			findPortfolio.setTotalUnit(findPortfolio.getTotalUnit() - totalUnit);
			transaction.setTotalUnit(findPortfolio.getTotalUnit() - totalUnit);
		}
		
		transaction.setId(0);
		transaction.setType(2);
		transaction.setMember(findMember);
		transaction.setDate(new Date());
		transaction.setTransactionStatus(transactionStatusRepo.findById(4).get());
		transactionRepo.save(transaction);
		
		return "Transaksi penjualan berhasil!";
	}
	
}
