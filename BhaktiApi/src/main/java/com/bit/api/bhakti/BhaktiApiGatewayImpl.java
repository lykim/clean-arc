package com.bit.api.bhakti;

import java.lang.reflect.Type;
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
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class BhaktiApiGatewayImpl implements BhaktiApiGateway{
	
	public final OkHttpClient client = new OkHttpClient();
	
	public void clean() {}

	public Page<Salesman> getSubordinateSalesmanBySalesmanCode(String salesmanCode) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.1.0.99:90/webAPI/MsSalesman/GetSubordinateList ").newBuilder();
		urlBuilder.addQueryParameter("spv", salesmanCode);
		String url = urlBuilder.build().toString();
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
		Request request = new Request.Builder()
		                     .url(url)
		                     .post(requestBody)
		                     .build();
		try {
			List<Salesman> salesmans = new ArrayList<>();
			Response response = client.newCall(request).execute();
			Type SALESMAN_TYPE = new TypeToken<ResponseApi<SalesmanResponseApi>>() {}.getType();
			ResponseApi<SalesmanResponseApi> responseApi = new Gson().fromJson(response.body().string(), SALESMAN_TYPE);
			if(responseApi.success) {
				salesmans = responseApi.data.stream().map(resp -> {
					return parseApiResponse(resp);
				}).collect(Collectors.toList());
				return new WebPage<>(salesmans, 0, 0);
			}else {
				throw new RuntimeException(responseApi.error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Salesman parseApiResponse(SalesmanResponseApi source) {
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
		salesman.setSupervisorCode(source.KODE_SUPERVISOR);
		return salesman;
	}

}
