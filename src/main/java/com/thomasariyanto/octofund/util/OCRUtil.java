package com.thomasariyanto.octofund.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

@Component
public class OCRUtil {
	
	public List<String> cleanResult(String ocrResult) {
		String[] cleanData = {"nik", "nama", "tempat/tgl lahir", "jenis kelamin", "alamat", "rt/rw", "kel/desa", "gol. darah", "agama", "status perkawinan", "pekerjaan", "kewarganegaraan", "berlaku hingga"};
		String replace = ocrResult.replace(":", "\n");
		String[] split = replace.split("\n");
		
		List<String> list = new ArrayList<String>();
		for (int i=0; i<split.length; i++){
			list.add(split[i]);
		}
		list.removeAll(Arrays.asList("", null));
		list.replaceAll(String::trim);
		list.removeIf(n -> (this.checkSmilarity(n, cleanData).getScore() > 0.8));
		
		return list;
	}
	
	public SmilarityValues checkSmilarity(String input, String[] data) {
		SmilarityValues result = new SmilarityValues("none", 0);
		
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		
		for (String s : data) {
			double score = service.score(input.toLowerCase(), s.toLowerCase());
			if(score > result.getScore()) {
				result.setScore(score);
				result.setWord(s.toUpperCase());
			}
		}
		
		return result;
	}
	
	public int checkKTPType(String input) {
		String[] sexData = {"laki-laki", "perempuan"};
		if(this.checkSmilarity(input, sexData).getScore() > 0.7) {
			return 1;
		}
		else {
			return 2;
		}
	}
	
	public String[] checkKTPData(String input) {
		String[] sexData = {"laki-laki", "perempuan"};
		String[] religionData = {"islam", "kristen", "katholik", "hindu", "buddha", "konghucu"};
		String[] maritalData = {"kawin", "belum kawin"};
		String[] jobData = {"pelajar/mahasiswa", "wirawasta", "pegawai swasta", "tidak bekerja"};
		
		if(this.checkSmilarity(input.toLowerCase(), sexData).getScore() > 0.8) {
			String[] result = {"sex", this.checkSmilarity(input.toLowerCase(), sexData).getWord()};
			return result;
		}
		if(this.checkSmilarity(input.toLowerCase(), religionData).getScore() > 0.8) {
			String[] result = {"religion", this.checkSmilarity(input.toLowerCase(), religionData).getWord()};
			return result;
		}
		if(this.checkSmilarity(input.toLowerCase(), maritalData).getScore() > 0.8) {
			String[] result = {"marital", this.checkSmilarity(input.toLowerCase(), maritalData).getWord()};
			return result;
		}
		if(this.checkSmilarity(input.toLowerCase(), jobData).getScore() > 0.8) {
			String[] result = {"job", this.checkSmilarity(input.toLowerCase(), jobData).getWord()};
			return result;
		}
		
		String[] result = {"unknown", "unknown"};
		return result;
	}
}
