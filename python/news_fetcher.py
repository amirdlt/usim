# import lxml.html
# from lxml import etree
# from lxml import html
# import validators
# import requests
#
#
# class NewsFetcher:
#
#     def __init__(self, base_url, reference_xpath, json_xpath, size=None):
#         self.base_url = base_url
#         self.reference_xpath = reference_xpath
#         self.json_xpath = json_xpath
#         self.size = size
#
#     def fetch(self):
#         tree = self.get_html_tree(base_url)
#         res = tree.xpath(reference_xpath)
#
#         if self.size is None:
#             size = len(res)
#         else:
#             size = min(self.size, len(res))
#
#         list_of_references = []
#         for i in range(size):
#             a = res[i].attrib['href']
#             list_of_references.append(a)
#
#         list_of_news = []
#         for url, json_values in zip(list_of_references, json_xpath):
#             try:
#                 news_data = self.get_news(url, json_values)
#             except requests.exceptions.ReadTimeout:
#                 print("Error in reading url : " + url)
#                 continue
#             news = News(url, news_data)
#             list_of_news.append(news)
#
#         return list_of_news
#
#     def get_news(self, news_url, json_values):
#         try:
#             html = self.get_html_tree(news_url)
#         except requests.exceptions.ReadTimeout:
#             raise requests.exceptions.ReadTimeout
#         result = dict()
#         for key, path in json_values.items():
#             value = html.xpath(path)
#             text = ''
#             if 'text()' in path:
#                 text = value[0]
#             else:
#                 for v in value:
#                     temp = ''.join(v.itertext())
#                     if len(temp.strip()) == 0:  # check result is empty or not
#                         continue
#                     text += temp + "\n"
#             result[key] = text
#         return result
#
#     @staticmethod
#     def get_html_tree(url, headers=None):
#         if not validators.url(url):
#             raise Exception('Url Format Exception for  "' + url + '"')
#
#         if headers is None:
#             headers = {
#                 "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"}
#         response = requests.get(url, headers=headers)
#         response = html.fromstring(response.content)
#         response.make_links_absolute(url)
#         tree = etree.HTML(html.tostring(response))
#         return tree
#
#
# class News:
#
#     def __init__(self, url, news_data):
#         self.url = url
#         self.title = ''
#         self.content = ''
#         self.data = ''
#         self.identification = ''
#         self.set_data(news_data)
#
#     def set_data(self, news_data):
#         for key in news_data.keys():
#             if key.lower() == 'title':
#                 self.title = news_data[key]
#             elif key.lower() == 'content':
#                 self.content = news_data[key]
#             elif key.lower() == 'date':
#                 self.data = news_data[key].replace('\r\n', "")
#             elif key.lower() == 'id':
#                 self.identification = news_data[key]
#
#     def __str__(self) -> str:
#         result = ''
#         result += 'Url : {' + self.url + '}\n'
#         result += 'Title : {' + self.title + '}\n'
#         result += 'Content : {' + self.content + '}\n'
#         result += 'Date : {' + self.data + '}\n'
#         result += 'ID : {' + self.identification + '}'
#         return result
#
#
# if __name__ == '__main__':
#     base_url = "https://www.usnews.com/topics/subjects/cybersecurity"
#     reference_xpath = "//h3[@data-reactid]/a"
#     size = 4
#     json_xpath = [{
#         "title": "//h1[@id='main-heading']",
#         "content": "//div[@data-component='text-block']",
#         "date": "//span/time[@data-testid]"
#     } for i in range(size)]
#
#     fetcher = NewsFetcher(base_url, reference_xpath, json_xpath, size)
#
#     results = fetcher.fetch()
#     size = len(results)
#     print(f'Size : {size}')
#
#     while True:
#         cmd = int(input(f'Enter Index From 1 to {size} : '))
#         if cmd == 0:
#             break
#         print(results[cmd - 1])
