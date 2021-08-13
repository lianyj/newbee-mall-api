
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsInfoMapper  extends BaseMapper<GoodsInfo> {



    GoodsInfo selectByCategoryIdAndName(@Param("GoodsInfoName") String GoodsInfoName, @Param("GoodsInfoCategoryId") Long GoodsInfoCategoryId);

    List<GoodsInfo> findGoodsList(PageQueryUtil pageUtil);

    int getTotalGoods(PageQueryUtil pageUtil);


    List<GoodsInfo> findGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalGoodsBySearch(PageQueryUtil pageUtil);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}