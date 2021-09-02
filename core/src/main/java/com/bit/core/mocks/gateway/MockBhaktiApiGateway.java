package com.bit.core.mocks.gateway;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bit.core.entity.Salesman;
import com.bit.core.gateway.BhaktiApiGateway;
import com.bit.core.response.SalesmanResponseApi;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.response.base.ResponseApi;
import com.bit.core.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MockBhaktiApiGateway implements BhaktiApiGateway{
	@Override
	public void clean() {}

	@Override
	public Page<Salesman> getSubordinateSalesmanBySalesmanCode(String salesmanCode) {
		try{
			List<Salesman> salesmans = new ArrayList<>();
			Reader reader = new FileReader(getFileFromResource("subordinate_salesman.json"));	
			Type SALESMAN_TYPE = new TypeToken<ResponseApi<SalesmanResponseApi>>() {}.getType();
			ResponseApi<SalesmanResponseApi> responseApi = new Gson().fromJson(reader, SALESMAN_TYPE);
			if(responseApi.success) {
				salesmans = responseApi.data.stream().map(resp -> {
					return parseApiResponse(resp, salesmanCode);
				}).collect(Collectors.toList());
				return new WebPage<>(salesmans, 0, 0);
			}else {
				throw new RuntimeException(responseApi.error);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Salesman parseApiResponse(SalesmanResponseApi source, String supervisorCode) {
		Salesman salesman = new Salesman();
		salesman.setActive("Y".equalsIgnoreCase(source.AKTIF) ? true : false);
		salesman.setArea(source.WILAYAH);
		salesman.setBranchCode(source.KODE_CABANG);
		salesman.setBranchName(source.NAMA_CABANG);
		salesman.setCode(StringUtils.isNotEmpty(source.KODE_SALESMAN) ? source.KODE_SALESMAN.trim() : source.KODE_SALESMAN);
		salesman.setDescription(source.KET);
		salesman.setDivision(source.DIVISI);
		salesman.setEmail(source.EMAIL);
		salesman.setLevel(source.LEVEL_SALESMAN);
		salesman.setLevelName(source.NAMA_LEVEL_SALESMAN);
		salesman.setName(source.NAMA_SALESMAN);
		salesman.setSupervisorCode(supervisorCode);
		return salesman;
	}
	
	private File getFileFromResource(String fileName) throws URISyntaxException{
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());
            return new File(resource.toURI());
        }

    }
	

}
