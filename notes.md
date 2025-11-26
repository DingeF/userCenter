前后端交互：
    前端通过以下技术与后端交互：
    1.AJAX
    2.AXIOS
    3.request:ant design pro中使用的请求库，封装了AJAX，提供了更方便的请求方式(更优于AXIOS)。

ant design pro项目中是通过app.ts文件来配置请求库的，如下所示：
export const request: RequestConfig = {
  prefix: 'localhost:8080',  # 后端服务地址
};

前端使用的端口是8000，后端使用的端口是8080。-----存在跨域问题
解决办法：
    WAY-1.在后端服务中添加跨域配置，允许前端服务的跨域请求。
    WAY-2.在前端服务中添加代理配置，将跨域请求代理到后端服务。
   
