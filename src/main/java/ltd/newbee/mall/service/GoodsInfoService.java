
package ltd.newbee.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface GoodsInfoService extends IService<GoodsInfo> {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveGoods(GoodsInfo goods);

    /**
     * 批量新增商品数据
     *
     * @param GoodsList
     * @return
     */
    void batchSaveGoods(List<GoodsInfo> GoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateGoods(GoodsInfo goods);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    GoodsInfo getGoodsById(Long id);

}
