package com.bonc.esexplore;
/*
* ZC的搜索引擎
*/
import com.bonc.esexplore.fulltextsearch.ESSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

@RestController
@CrossOrigin(origins = "*")
@SpringBootApplication
public class EsExploreApplication {
	@Autowired
	public static ESSearch esClient;

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication.run(EsExploreApplication.class, args);
		ESSearch.getESClient();
	}
}
