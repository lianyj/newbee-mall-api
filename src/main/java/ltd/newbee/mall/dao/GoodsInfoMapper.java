
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsInfoMapper  extends BaseMapper<GoodsInfo> {

    int deleteByPrimaryKey(Long GoodsInfoId);

    int insert(GoodsInfo record);

    int insertSelective(GoodsInfo record);

    GoodsInfo selectByPrimaryKey(Long GoodsInfoId);

    GoodsInfo selectByCategoryIdAndName(@Param("GoodsInfoName") String GoodsInfoName, @Param("GoodsInfoCategoryId") Long GoodsInfoCategoryId);

    int updateByPrimaryKeySelective(GoodsInfo record);

    int updateByPrimaryKeyWithBLOBs(GoodsInfo record);

    int updateByPrimaryKey(GoodsInfo record);

    List<GoodsInfo> findGoodsList(PageQueryUtil pageUtil);

    int getTotalGoods(PageQueryUtil pageUtil);

    List<GoodsInfo> selectByPrimaryKeys(List<Long> GoodsInfoIds);

    List<GoodsInfo> findGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("GoodsInfoList") List<GoodsInfo> GoodsInfoList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}