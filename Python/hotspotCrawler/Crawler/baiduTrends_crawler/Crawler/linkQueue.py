#encoding=utf-8
class linkQueue:
    def __init__(self):
        self.visted=[]
        
        self.unVisited=[]

    def getVisitedUrl(self):
        return self.visted

    def getUnvisitedUrl(self):
        return self.unVisited

    def addVisitedUrl(self,url):
        self.visted.append(url)

    def removeVisitedUrl(self,url):
        self.visted.remove(url)

    def unVisitedUrlPop(self):
        try:
            return self.unVisited.pop()
        except:
            return None

    def addUnvisitedUrl(self,url):
        if url!="" and url not in self.visted and url not in self.unVisited:
            self.unVisited.insert(0,url)

    def getVisitedUrlCount(self):
        return len(self.visted)

    def getUnvistedUrlCount(self):
        return len(self.unVisited)

    def unVisitedUrlsEnmpy(self):
        return len(self.unVisited)==0
    