package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.gb28181.bean.GbStream;
import com.genersoft.iot.vmp.gb28181.bean.ParentPlatform;
import com.genersoft.iot.vmp.gb28181.bean.PlatformCatalog;
import com.genersoft.iot.vmp.gb28181.bean.PlatformGbStream;
import com.genersoft.iot.vmp.media.zlm.dto.StreamProxyItem;
import com.genersoft.iot.vmp.media.zlm.dto.StreamPushItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface PlatformGbStreamMapper {

    @Insert("REPLACE INTO wvp_platform_gb_stream (gb_stream_id, platform_id, catalog_id) VALUES" +
            "( #{gbStreamId}, #{platformId}, #{catalogId})")
    int add(PlatformGbStream platformGbStream);


    @Insert("<script> " +
            "INSERT into wvp_platform_gb_stream " +
            "(gb_stream_id, platform_id, catalog_id) " +
            "values " +
            "<foreach collection='streamPushItems' index='index' item='item' separator=','> " +
            "(#{item.gbStreamId}, #{item.platform_id}, #{item.catalogId})" +
            "</foreach> " +
            "</script>")
    int batchAdd(List<StreamPushItem> streamPushItems);

    @Delete("DELETE from wvp_platform_gb_stream WHERE gb_stream_id = (select gb_stream_id from wvp_gb_stream where app=#{app} AND stream=#{stream})")
    int delByAppAndStream(String app, String stream);

    @Delete("DELETE from wvp_platform_gb_stream WHERE platform_id=#{platformId}")
    int delByPlatformId(String platformId);

    @Select("SELECT " +
            "pp.* " +
            "FROM " +
            "wvp_platform_gb_stream pgs " +
            "LEFT JOIN wvp_platform pp ON pp.server_gb_id = pgs.platform_id " +
            "LEFT join wvp_gb_stream gs ON gs.gb_stream_id = pgs.gb_stream_id " +
            "WHERE " +
            "gs.app =#{app} " +
            "AND gs.stream =#{stream} ")
    List<ParentPlatform> selectByAppAndStream(String app, String stream);

    @Select("SELECT pgs.*, gs.gb_id  from wvp_platform_gb_stream pgs " +
            "LEFT join wvp_gb_stream gs ON pgs.gb_stream_id = gs.gb_stream_id  " +
            "WHERE gs.app=#{app} AND gs.stream=#{stream} AND pgs.platform_id=#{serverGBId}")
    StreamProxyItem selectOne(String app, String stream, String serverGBId);

    @Select("select gs.* \n" +
            "from wvp_gb_stream gs\n" +
            "    left join wvp_platform_gb_stream pgs\n" +
            "        on gs.gb_stream_id = pgs.gb_stream_id\n" +
            "where pgs.platform_id=#{platformId} and pgs.catalog_id=#{catalogId}")
    List<GbStream> queryChannelInParentPlatformAndCatalog(String platformId, String catalogId);

    @Select("select gs.gb_id as id, gs.name as name, pgs.platform_id as platform_id, pgs.catalog_id as catalog_id , 0 as children_count, 2 as type\n" +
            "from wvp_gb_stream gs\n" +
            "    left join wvp_platform_gb_stream pgs\n" +
            "        on gs.gb_stream_id = pgs.gb_stream_id\n" +
            "where pgs.platform_id=#{platformId} and pgs.catalog_id=#{catalogId}")
    List<PlatformCatalog> queryChannelInParentPlatformAndCatalogForCatalog(String platformId, String catalogId);

    @Delete("DELETE from wvp_platform_gb_stream WHERE catalog_id=#{id}")
    int delByCatalogId(String id);

    @Select("<script> " +
            "SELECT " +
            "pp.* " +
            "FROM " +
            "wvp_platform pp " +
            "left join wvp_platform_gb_stream pgs on " +
            "pp.server_gb_id = pgs.platform_id " +
            "left join wvp_gb_stream gs " +
            "on gs.gb_stream_id = pgs.gb_stream_id " +
            "WHERE " +
            "gs.app = #{app} " +
            "AND gs.stream = #{stream}" +
            "AND pp.server_gb_id IN" +
            "<foreach collection='platforms'  item='item'  open='(' separator=',' close=')' > #{item}</foreach>" +
            "</script> ")
    List<ParentPlatform> queryPlatFormListForGBWithGBId(String app, String stream, List<String> platforms);

    @Delete("DELETE from wvp_platform_gb_stream WHERE gb_stream_id = (select id from wvp_gb_stream where app=#{app} AND stream=#{stream}) AND platform_id=#{platformId}")
    int delByAppAndStreamAndPlatform(String app, String stream, String platformId);

    @Delete("<script> "+
            "DELETE from wvp_platform_gb_stream where gb_stream_id in " +
            "<foreach collection='gbStreams' item='item' open='(' separator=',' close=')' >" +
            "#{item.gbStreamId}" +
            "</foreach>" +
            "</script>")
    void delByGbStreams(List<GbStream> gbStreams);

    @Delete("<script> "+
            "DELETE from wvp_platform_gb_stream where platform_id=#{platformId} and gb_stream_id in " +
            "<foreach collection='gbStreams' item='item' open='(' separator=',' close=')'>" +
            "#{item.gbStreamId} " +
            "</foreach>" +
            "</script>")
    void delByAppAndStreamsByPlatformId(List<GbStream> gbStreams, String platformId);

    @Delete("DELETE from wvp_platform_gb_stream WHERE platform_id=#{platformId} and catalog_id=#{catalogId}")
    int delByPlatformAndCatalogId(String platformId, String catalogId);
}
