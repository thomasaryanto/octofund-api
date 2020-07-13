package com.thomasariyanto.octofund.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.thomasariyanto.octofund.dao.MemberRepo;
import com.thomasariyanto.octofund.dao.MutualFundRepo;
import com.thomasariyanto.octofund.dao.PortfolioRepo;
import com.thomasariyanto.octofund.dao.TransactionRepo;
import com.thomasariyanto.octofund.dao.TransactionStatusRepo;
import com.thomasariyanto.octofund.entity.Member;
import com.thomasariyanto.octofund.entity.MutualFund;
import com.thomasariyanto.octofund.entity.Portfolio;
import com.thomasariyanto.octofund.entity.Transaction;
import com.thomasariyanto.octofund.util.EmailUtil;
import com.thomasariyanto.octofund.util.UploadUtil;

@RestController
@RequestMapping("/transactions")
@CrossOrigin
public class TransactionController {
	
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
	private EmailUtil emailUtil;
	
	@Autowired
	private UploadUtil fileUploader;
	
	@GetMapping
	public Iterable<Transaction> getTransactions() {
		return transactionRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public Transaction getTransactionById(@PathVariable int id) {
		return transactionRepo.findById(id).get();
	}
	
	@GetMapping("/member/{memberId}")
	public Iterable<Transaction> getTransactionByMemberId(@PathVariable int memberId) {
		return transactionRepo.findAllByMemberId(memberId);
	}
	
	@GetMapping("/manager/{managerId}")
	public Page<Transaction> getTransactionByManagerId(@PathVariable int managerId, Pageable pageable) {
		return transactionRepo.findAllByTransactionStatusIdAndMutualFundManagerId(2, managerId, pageable);
	}
	
	@PostMapping("/buy")
	public Transaction addBuyTransaction(@Valid @RequestBody Transaction transaction) {
		transaction.setId(0);
		transaction.setType(1);
		transaction.setTransactionStatus(transactionStatusRepo.findById(1).get());
		transaction.setDate(new Date());
		return transactionRepo.save(transaction);
	}
	
	@GetMapping("/buy/payment/{transactionId}")
	public Transaction paymentBuyTransaction(@PathVariable int transactionId) {
		return transactionRepo.findByTypeAndId(1, transactionId);
	}
	
	@PostMapping("/buy/confirm/{transactionId}")
	public Transaction confirmBuyTransaction(@RequestParam("file") MultipartFile file, @PathVariable int transactionId) {
		String paymentProofPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\payment\\";
		String paymentProof = fileUploader.uploadFile(file, paymentProofPath);
		String paymentProofUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("transactions/paymentproof/").path(paymentProof).toUriString();
		
		Transaction findTransaction = transactionRepo.findById(transactionId).get();
		findTransaction.setTransactionStatus(transactionStatusRepo.findById(2).get());
		findTransaction.setRejectMessage(null);
		findTransaction.setPaymentProof(paymentProofUri);
		return transactionRepo.save(findTransaction);
	}
	
	@GetMapping("/paymentproof/{fileName:.+}")
	public ResponseEntity<Object> getProof(@PathVariable String fileName){
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
	
	@PostMapping("/buy/reject")
	public String rejectBuyTransaction(@RequestBody Transaction transaction) {
		Transaction findTransaction = transactionRepo.findById(transaction.getId()).get();
		Member findMember = memberRepo.findById(findTransaction.getMember().getId()).get();

		findTransaction.setTransactionStatus(transactionStatusRepo.findById(3).get());
		findTransaction.setRejectMessage(transaction.getRejectMessage());
		transactionRepo.save(findTransaction);
		this.emailUtil.sendEmail(findMember.getUser().getEmail(), "Transaksi Pembelian Reksadana Gagal", "Pembayaran kamu untuk pembelian reksadana "+findTransaction.getMutualFund().getName()+" sebesar Rp. "+findTransaction.getTotalPrice()+" ditolak dikarenakan: "+transaction.getRejectMessage()+".");
		return "Transaksi berhasil ditolak!";
	}
	
	@PostMapping("/buy/accept")
	public String acceptBuyTransaction(@RequestBody Transaction transaction) {
		Transaction findTransaction = transactionRepo.findById(transaction.getId()).get();
		Member findMember = memberRepo.findById(findTransaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(findTransaction.getMutualFund().getId()).get();
		Optional<Portfolio> checkPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(findTransaction.getMember().getId(), findTransaction.getMutualFund().getId());
		
		if(checkPortfolio.toString() == "Optional.empty") {
			Portfolio portfolio = new Portfolio();
			portfolio.setMember(findMember);
			portfolio.setMutualFund(findMutualFund);
			portfolio.setTotalInvest(findTransaction.getTotalPrice());
			portfolio.setTotalUnit(findTransaction.getTotalPrice() / findMutualFund.getLastPrice());
			portfolioRepo.save(portfolio);
		}
		else {
			Portfolio portfolio = checkPortfolio.get();
			portfolio.setTotalInvest(portfolio.getTotalInvest() + findTransaction.getTotalPrice());
			portfolio.setTotalUnit(portfolio.getTotalUnit() + (findTransaction.getTotalPrice() / findMutualFund.getLastPrice()));
			portfolioRepo.save(portfolio);
		}
		
		findTransaction.setTransactionStatus(transactionStatusRepo.findById(4).get());
		transactionRepo.save(findTransaction);
		
		this.emailUtil.sendEmail(findMember.getUser().getEmail(), "Transaksi Pembelian Reksadana Berhasil", "Transaksi pembelian reksadana "+findTransaction.getMutualFund().getName()+" sebesar Rp. "+findTransaction.getTotalPrice()+" telah berhasil!");
		

		return "Transaksi berhasil dikonfirmasi!";
	}
	
	@PostMapping("/sell")
	public Transaction addTransaction(@Valid @RequestBody Transaction transaction) {
		Member findMember = memberRepo.findById(transaction.getMember().getId()).get();
		MutualFund findMutualFund = mutualFundRepo.findById(transaction.getMutualFund().getId()).get();
		Portfolio findPortfolio = portfolioRepo.findByMemberIdAndMutualFundId(transaction.getMember().getId(), transaction.getMutualFund().getId()).get();
		
		if(transaction.getTotalUnit() > findPortfolio.getTotalUnit()) {
			throw new RuntimeException("Jumlah penjualan tidak mencukupi!");
		}
		else if(transaction.getTotalUnit() == findPortfolio.getTotalUnit()) {
			portfolioRepo.delete(findPortfolio);
		}
		else {
			if (findPortfolio.getTotalInvest() - (transaction.getTotalUnit() * findMutualFund.getLastPrice()) < 0) {
				findPortfolio.setTotalInvest(0);
			}
			else {
				findPortfolio.setTotalInvest(findPortfolio.getTotalInvest() - (long)(transaction.getTotalUnit() * findMutualFund.getLastPrice()));
			}
			findPortfolio.setTotalUnit(findPortfolio.getTotalUnit() - transaction.getTotalUnit());
		}
		
		transaction.setId(0);
		transaction.setType(2);
		transaction.setMember(findMember);
		transaction.setDate(new Date());
		return transactionRepo.save(transaction);
	}
}
