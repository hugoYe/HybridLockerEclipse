#encoding=utf-8
from linkQueue import linkQueue
import socket
import re
import MySQLdb
from selenium import webdriver
import time
import sys
reload(sys)

class MyCrawler:
    
    global conn 
    conn = MySQLdb.connect(
                    host = "localhost",
                    port = 3306,
                    user = "root",
                    passwd = "cxc705296",
                    db = "googletrends",
                    charset="utf8"
                       )
    
    def __init__(self,seeds):
        #使用种子初始化url队列
        self.linkQueue=linkQueue()
        
        if isinstance(seeds,str):
            self.linkQueue.addUnvisitedUrl(seeds)
        if isinstance(seeds,list):
            for i in seeds:
                self.linkQueue.addUnvisitedUrl(i)
        print "Add the seeds url \"%s\" to the unvisited url list"%str(self.linkQueue.unVisited)
    #抓取过程主函数
    def crawling(self,seeds):
        #清空表中的内容\
        global conn
        cur = conn.cursor()
        sqlclear = "delete from keyword"
        cur.execute(sqlclear)
        while self.linkQueue.unVisitedUrlsEnmpy() is False :
            #队头url出队列
            visitUrl=self.linkQueue.unVisitedUrlPop()
            print "Pop out one url \"%s\" from unvisited url list"%visitUrl
            self.getPageSource(visitUrl)
            if visitUrl is None or visitUrl=="":
                continue
            
            #将url放入已访问的url中
            self.linkQueue.addVisitedUrl(visitUrl)
            print "Visited url count: "+str(self.linkQueue.getVisitedUrlCount())
            
            print "Unvisited url count: "+str(self.linkQueue.getUnvistedUrlCount())
        conn.commit()
        conn.close()   

    #获取网页源码
    def getPageSource(self,url,timeout=100):
        global conn
        cur = conn.cursor()
        try:
            socket.setdefaulttimeout(timeout)
            browser = webdriver.PhantomJS()
            browser.get(url)
            page = browser.page_source
            countryPattern = re.compile(r'<span class="popup-picker-anchor-caption" id="geo-picker-button_caption">(.+?)</span>')
            keywordPattern = re.compile(r'<span class="hottrends-single-trend-title ellipsis-maker-inner">(.+?)</span>',re.DOTALL)
            countrymatch = re.search(countryPattern, page)
            keywordmatch = re.findall(keywordPattern, page)
            country =  countrymatch.group(1)
            sqlinsert = "insert into keyword values(%s,%s,%s)" 
            no = 0
            for item in keywordmatch:
                no += 1
                cur.execute(sqlinsert, (country, no, re.sub('<span.+>','',item)))
            browser.quit()
        except Exception,e:            
            print str(e)
            browser.quit()


        
    #获取页面数据