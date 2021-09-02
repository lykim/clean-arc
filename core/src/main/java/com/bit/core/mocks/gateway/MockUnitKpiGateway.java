package com.bit.core.mocks.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.Direction;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.StringUtils;

public class MockUnitKpiGateway implements UnitKpiGateway{

	private Set<UnitKpi> unitKpis = MockStorage.unitKpis;
	private static UnitKpiGateway INSTANCE;
	
	private MockUnitKpiGateway() {}
	
	public static UnitKpiGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockUnitKpiGateway();
		}
		return INSTANCE;
	}
	@Override
	public UnitKpi findById(String id) {
		Optional<UnitKpi> optional = unitKpis.stream().filter(data -> id.equals(data.getId())).findAny();
		UnitKpi entity = optional.isPresent() ? optional.get() : null;
		return entity;
	}

	@Override
	public void save(UnitKpi entity) {
		unitKpis.add(entity);
	}

	@Override
	public void update(UnitKpi entity, String id) {
		UnitKpi unitKpi = findById(id);
		unitKpi.setName(entity.getName());
		unitKpi.setDescription(entity.getDescription());
	}

	@Override
	public Page<UnitKpi> find(FindManyRequestModel param) {
		List<UnitKpi> listUnitKpis = new ArrayList<>();
		if(param.direction == Direction.DESCENDING) {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("name")) {
					listUnitKpis = unitKpis.stream().sorted(Comparator.comparing(UnitKpi::getName).reversed() ).collect(Collectors.toList());								
				}
			}else {
				listUnitKpis = unitKpis.stream().sorted(Comparator.comparing(UnitKpi::getName).reversed() ).collect(Collectors.toList());							
			}
		}else {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("name")) {
					listUnitKpis = unitKpis.stream().sorted(Comparator.comparing(UnitKpi::getName)).collect(Collectors.toList());			
				}
			}else {
				listUnitKpis = unitKpis.stream().sorted(Comparator.comparing(UnitKpi::getName)).collect(Collectors.toList());							
			}

		}
		FindManyUnitKpiRequestModel unitKpiParam = (FindManyUnitKpiRequestModel)param;
		if(StringUtils.isNotEmpty(unitKpiParam.nameLike)) {
			listUnitKpis = listUnitKpis.stream().filter(data -> data.getName().contains(unitKpiParam.nameLike)).collect(Collectors.toList());
		}
		int unitKpisSize = listUnitKpis.size();
		List<UnitKpi> results = listUnitKpis.stream().collect(Collectors.toList());
		Collections.copy(results, listUnitKpis);
		if(param.pageSize > 0) {
			int indexStart = ((param.pageNumber * param.pageSize) - param.pageSize) + 1;
			indexStart = indexStart - 1;
			int indexEnd = indexStart + param.pageSize;
			indexEnd = results.size() < indexEnd ? results.size() : indexEnd;
			results = listUnitKpis.subList(indexStart, indexEnd);
		}
		Page<UnitKpi> page = new WebPage<>(results, unitKpisSize, param.pageSize);
		return page;
	}

	@Override
	public void clean() {
		unitKpis.clear();
	}

	@Override
	public void setActive(String id, boolean active) {
		UnitKpi unitKpi = findById(id);
		unitKpi.setActive(active);
	}

	@Override
	public UnitKpi findByName(String name) {
		Optional<UnitKpi> optional = unitKpis.stream().filter(data -> name.equals(data.getName())).findAny();
		return optional.isPresent() ? optional.get() : null;
	}

}
