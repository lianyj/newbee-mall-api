
package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.service.GoodsInfoService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class GoodsInfoServiceImpl  extends ServiceImpl<GoodsInfoMapper, GoodsInfo> implements GoodsInfoService {

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Override
    public PageResult getGoodsPage(PageQueryUtil pageUtil) {
        List<GoodsInfo> goodsList = goodsInfoMapper.findGoodsList(pageUtil);
        int total = goodsInfoMapper.getTotalGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveGoods(GoodsInfo goods) {
        if (goodsInfoMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveGoods(List<GoodsInfo> GoodsList) {
        if (!CollectionUtils.isEmpty(GoodsList)) {
            goodsInfoMapper.batchInsert(GoodsList);
        }
    }

    @Override
    public String updateGoods(GoodsInfo goods) {
        GoodsInfo temp = goodsInfoMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        goods.setUpdateTime(new Date());
        if (goodsInfoMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsInfo getGoodsById(Long id) {
        return goodsInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsInfoMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

}
