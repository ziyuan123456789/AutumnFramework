package org.example.mapper;


import org.example.Bean.HardwareSetting;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyParam;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyMapper
public interface HardwareSettingMapper {
    @MySelect("select * from HardwareSetting where HardwareSettingId = #{HardwareSettingId}")
    HardwareSetting getOneHardware(@MyParam("HardwareSettingId") Integer HardwareSettingId);
}
