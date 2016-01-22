#encoding=utf-8
from MyCrawler import MyCrawler
def main(seeds):
    craw=MyCrawler(seeds)
    craw.crawling(seeds)
    
if __name__=="__main__":
    main(['http://top.baidu.com/buzz?b=1','http://top.baidu.com/buzz?b=341&c=513'])