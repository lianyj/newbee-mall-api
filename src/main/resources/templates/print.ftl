<!DOCTYPE html>
<html lang="en">
<head>
    <title>SpringBoot + Freemarker</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <style>
        .head_title{
            text-align: center;
            font-size: 20pt;
            margin-bottom: 5px;
        }
        b{
            font-weight: bold;
        }
        .l-tr{
            font-size: 12pt;
            float:left;
        }
        .r-tr{
            font-size: 12pt;
            float:right;
        }
        .l_tr_title{
            font-size: 12pt;
            text-align: right;
            display: inline-block;
            vertical-align: top;
            width: 100px;
            padding-bottom: 5px;
        }
        .l_tr_content_full{
            font-size: 12pt;
            text-align: left;
            display: inline-block;
            vertical-align: top;
            width: 200px;
            padding-bottom: 5px;
        }
        table {
            font-size: 12pt;
            width: 100%;
            border-collapse: collapse;/*边框合并*/
        }
        th {
            font-size: 12pt;
            text-align: center;
            padding: .5em .5em;
            font-weight: bold;
        }

        td {
            font-size: 12pt;
            text-align: center;
            padding: .5em .5em;
            border-bottom: solid 1px #ccc;
        }

        table,table tr th, table tr td { border:1px solid ;align:center;}/*设置边框*/
    </style>
</head>
<body>
<#list 0..1 as i>
    <div class="head_title">
       <span><b>杭州诚必达光学有限公司</b></span>
    </div>

    <div style="margin-top: 50px">
        <div class="l-tr" >
            <span class="l_tr_title">客户名称:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.userName??> ${orderDetailVO.userName}</#if></span>
        </div>
        <div class="r-tr">
            <span class="l_tr_title">订单编号:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.orderNo??> ${orderDetailVO.orderNo}</#if></span>
        </div>
        <div class="l-tr">
            <span class="l_tr_title">联系人:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.contactName??> ${orderDetailVO.contactName}</#if></span>
            </div>
        <div class="r-tr">
            <span class="l_tr_title">订单日期:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.orderDate?? && orderDetailVO.orderDate?date> ${orderDetailVO.orderDate?string('yyyy-MM-dd')}</#if></span>
        </div>
        <div class="l-tr">
            <span class="l_tr_title">联系电话:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.mobile??> ${orderDetailVO.mobile}</#if></span>
            </div>
        <div class="r-tr">
            <span class="l_tr_title">快递费用:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.expressFee??> ${orderDetailVO.expressFee}</#if></span>
        </div>
        <div class="l-tr">
            <span class="l_tr_title">联系地址:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.address??> ${orderDetailVO.address}</#if></span>
             </div>
        <div class="r-tr">
            <span class="l_tr_title">备注:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.remark??> ${orderDetailVO.remark}</#if></span>
        </div>
    </div>
    <table>
        <tr>
            <th>产品名称/规格型号</th>
            <th>单位</th>
            <th>数量</th>
            <th>单价</th>
            <th>折后金额</th>
        </tr>
        <#if orderDetailVO.orderItemVOS??>
            <#list orderDetailVO.orderItemVOS as list>
                <tr>
                    <td> <#if list.goodsName??> ${list.goodsName}</#if> <#if list.goodsIntro??> ${list.goodsIntro}</#if></td>
                    <td> <#if list.goodsUnit??> ${list.goodsUnit}</#if></td>
                    <td> <#if list.goodsCount??> ${list.goodsCount}</#if></td>
                    <td> <#if list.originalPrice??> ${list.originalPrice}</#if></td>
                    <td> <#if list.sellingPrice??> ${list.sellingPrice}</#if></td>
                </tr>
            </#list>
        </#if>
    </table>
    <div  style="margin-top: 10px">
        <div class="l-tr">
            <span class="l_tr_title">制单人:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.printName??> ${orderDetailVO.printName}</#if></span>
        </div>
        <div class="r-tr">
            <span class="l_tr_title">打印日期:</span>
            <span class="l_tr_content_full"><#if orderDetailVO.printTime??> ${orderDetailVO.printTime}</#if></span>
        </div>
    </div>


    <#if i==0>
        <br/>
        -
        <br/>
    </#if>
</#list>


</body>
</html>
<script type="text/JavaScript">
    window.print();
</script>
