import requests
import xlwt
from bs4 import BeautifulSoup

# 解析首页每个城市
def parseAllCities(url):
    html = requests.get(url, headers=headers).text
    bs = BeautifulSoup(html, 'lxml')
    cities = bs.find_all('li', class_='mp-sidebar-item')  # 所有城市标签
    cities = cities[19:20]  # 取前3个城市(北京，上海，成都)

    print('城市检验：', cities)
    global currentCityIndex
    for city in cities:
        currentCityIndex = currentCityIndex + 1
        cityName = city.a.string  # 城市名
        print('城市名校验：', cityName)
        cityUrl = 'http://piao.qunar.com' + city.a.get('href')
        cityUrl = cityUrl.replace('®', '&reg')  # 避免转义
        print(cityName, ':\n')

        html2 = requests.get(cityUrl, headers=headers).text
        bs2 = BeautifulSoup(html2, 'lxml')
        tags = bs2.find_all(attrs={"data-key": "subject"})
        tags = tags[1:7]
        print('主题检验：', tags)
        global currentTagIndex
        for tag in tags:
            currentTagIndex = currentTagIndex + 1
            global currentRowIndex
            currentRowIndex = 2
            tagName = tag.a.span.string
            tagName = tagName.split('(',1)[0]
            print('主题名校验：', tagName)

            cityTagUrl = cityUrl + '&subject=' + tagName
            cityTagUrl = cityTagUrl.replace('®', '&reg')  # 避免转义
            print(tagName, ':\n')
            global currentSheet
            currentSheet = workBook.add_sheet('sheet' + str(currentTagIndex), cell_overwrite_ok=True)
            # 填写表头（景区名称，城市，主题，地址，简介，月销量，图片链接，价格，详情链接，等级，热度）
            tableList = ['景区名称', '城市', '主题', '地址', '简介', '月销量', '图片链接', '价格', '详情链接', '等级', '热度']
            for i, val in enumerate(tableList):
                currentSheet.write(1, i, val)
            parseSingleCity(cityName, tagName, cityTagUrl)
            print('\n\n\n')


#
#
#
# 解析每个单一City的单一Tag(解析多页)
def parseSingleCity(cityName, tagName, cityTagUrl):
    # 解析城市tag前3页内容
    global pageSize
    for i in range(1, 1 + pageSize):
        pageUrl = cityTagUrl + '&page=' + str(i)
        print('第%d页:' % (i))
        print('网址：:', pageUrl)
        parsePageInfo(pageUrl, cityName, tagName)  # 解析具体信息

#
# 解析每一页的具体信息
def parsePageInfo(pageUrl, sightCity, sightTag):
    html = requests.get(pageUrl, headers=headers).text
    bs = BeautifulSoup(html,'lxml')
    # # 获取景区图片
    # for s in bs.find_all(attrs={"data-click-type": "l_pic"}):
    #     for img in s.find_all('img', src=True):
    #         sightPicUrl = img.get('src')


    sightList = bs.find_all('div', class_='sight_item')  # 所有景点信息
    # 遍历每个景点
    for sight in sightList:
        sightName = sight.find_all('a', class_='name')[0].string  # 景区名
        # 景区等级，有些景区无等级所以可能异常
        try:
            sightLevel = sight.find_all('span', class_='level')[0].string
        except:
            sightLevel = '无'
        sightAddress = sight.find_all('p', class_='address')[0].span.string[3:]  # 地址，去掉'地址'二字
        sightDesc = sight.find_all('div', class_='intro')[0].string  # 景区介绍
        # 获取景区最低价格
        try:
            sightPrice = sight.find_all('span', class_='sight_item_price')[0].em.string
        except:
            sightPrice = '免费'
        # 获取月销量
        try:
            sightNum = sight.find_all('span', class_='hot_num')[0].string
        except:
            sightNum = 0

        # 获取景区热度
        sightStarLevel = sight.find_all('span', class_='product_star_level')[0].text[2:]
        # 获取景区详细页Url
        sightDetailUrl = sight.find_all('a', class_='sight_item_do')[0].get('href')
        baseUrl = 'http://piao.qunar.com'
        sightDetailUrl = baseUrl + sightDetailUrl

        sightas = sight.find_all(attrs={"data-click-type": "l_pic"})
        sightPicUrl = sightas[0].find_all("img")[0].get('data-original')
        # for sighta in sightas:
        #     sightPicUrl = sighta.find_all('img')[0].get('src')

        # 打印结果
        print('{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10}'.format(sightName, sightCity, sightTag, sightAddress, sightDesc, sightNum, sightPicUrl, sightPrice, sightDetailUrl, sightLevel, sightStarLevel))
        # 结果导入excel
        tableList = [sightName, sightCity, sightTag, sightAddress, sightDesc, sightNum, sightPicUrl, sightPrice, sightDetailUrl, sightLevel, sightStarLevel]
        global currentSheet
        global currentRowIndex
        for i, val in enumerate(tableList):
            currentSheet.write(currentRowIndex, i, val)  # 填写每一行数据
        currentRowIndex = currentRowIndex + 1

#

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36'
}
cityNum = 2  # 查询的城市数
pageSize = 3  # 查询的页数
startUrl = 'http://piao.qunar.com/'
currentCityIndex = 0  # 当前处理的城市下标
currentTagIndex = 0  # 当前处理的景点主题下标
currentRowIndex = 2  # 当前在excel中的行号
workBook = xlwt.Workbook()  # 创建excel表格
currentSheet = None
print('开始')
parseAllCities(startUrl)
workBook.save('testPy.xls')
