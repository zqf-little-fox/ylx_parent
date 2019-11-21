package cn.zqf.core.entity;

import cn.zqf.core.pojo.specification.Specification;
import cn.zqf.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

public class SpecEntity implements Serializable {
    private Specification specification;
    private List<SpecificationOption> specificationOptionList;

    public SpecEntity() {
    }

    public SpecEntity(Specification specification, List<SpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

}
