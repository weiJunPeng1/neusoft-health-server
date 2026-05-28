package com.neusoft.health.common.enums;

import lombok.Getter;

@Getter
public enum ConsultCategoryEnum {

    INTERNAL("内科", "感冒发烧、咳嗽、胃肠不适、高血压、糖尿病等内科相关"),
    SURGERY("外科", "外伤、骨折、烧伤、肿块等外科相关"),
    PEDIATRICS("儿科", "儿童发热、发育、疫苗、儿童用药等儿科相关"),
    GYNECOLOGY("妇产科", "月经不调、孕期保健、妇科疾病等妇产科相关"),
    DERMATOLOGY("皮肤科", "皮疹、过敏、湿疹、痤疮等皮肤相关"),
    OPHTHALMOLOGY("眼科", "视力问题、眼部不适、干眼症等眼科相关"),
    ORTHOPEDICS("骨科", "腰腿痛、颈椎病、关节炎等骨科相关"),
    NEUROLOGY("神经科", "头痛、失眠、焦虑、抑郁等神经精神相关"),
    NUTRITION("营养饮食", "饮食建议、营养搭配、减肥增重等营养相关"),
    OTHER("其他", "不属于以上分类的健康咨询");

    private final String label;
    private final String description;

    ConsultCategoryEnum(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static ConsultCategoryEnum fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return OTHER;
        }
        for (ConsultCategoryEnum c : values()) {
            if (c.label.equals(label.trim())) {
                return c;
            }
        }
        for (ConsultCategoryEnum c : values()) {
            if (label.contains(c.label)) {
                return c;
            }
        }
        return OTHER;
    }
}
