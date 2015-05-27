<%-- 
    Document   : list
    Created on : 22.5.2015, 16:09:21
    Author     : Game
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Syntactic Bush Visualisation</title>
        <script type="text/javascript">
            function simulatePathDraw(path) {
                var length = path.getTotalLength();
                path.style.transition = path.style.WebkitTransition = 'none';
                path.style.strokeDasharray = length + ' ' + length;
                path.style.strokeDashoffset = length;
                path.getBoundingClientRect();
                path.style.transition = path.style.WebkitTransition = 'stroke-dashoffset 2s ease-in-out';
                path.style.transitionDelay = "1s";
                path.style.strokeDashoffset = '0';
            }

            function getAllPaths() {
                var paths = document.getElementsByTagName("path");
                return paths;
            }

            function drawPaths(paths) {
                for (i = 0; i < paths.length; i++) {
                    simulatePathDraw(paths[i]);
                }
            }

        </script>
    </head>
    <body>
        <h1>Syntactic Bush Visualisation</h1>
        <br/>
        <br/>
        <h2>Select package:</h2>
        <br/>
        <br/>
        <form  method="post" align="center" enctype="multipart/form-data">
            <select name="pack" id="pack">
                <option value="01">data01</option>
                <option value="02">data02</option>
            </select>
        </form>
        <hr width="75%" align="center" size="1px" >

        <object width="1000px" height="400px">

            ${currentSentence}
        </object>
        <p id="paragraph">${indexOfCurrentSentence}</p>
        <form id="footer">

            <button name="prevButton" type="submit" form="footer" formaction="${pageContext.request.contextPath}/bush/prev" formmethod="post">Previous</button>
            <button name="nextButton" type="submit" form="footer" formaction="${pageContext.request.contextPath}/bush/next" formmethod="post">Next</button>    
        </form>

        <form id="centerFooter">
            <button  id="gotoB" name="gotoButton" type="submit" form="centerFooter" formaction="${pageContext.request.contextPath}/bush/goto" formmethod="post">Go to</button>
            <font id="sentenceN">sentence n°</font>
            <input id="gotoI" type="text" name="gotoField" />
        </form>

        <div>

        </div>

    </body>



    <style>

        svg {display: block;
             margin-left: auto;
             margin-right: auto;
             margin-top: auto;
             margin-bottom: auto;}

        h1,h2 {
            text-align: center;
        }
        h2 {
            font-family:Georgia,serif;
            color:#4E443C;
            font-variant: small-caps; text-transform: none; font-weight: 100; margin-bottom: 0;

        }
        h1 { font-family: times, Times New Roman, times-roman, georgia, serif;
             color: #444;
             margin: 0;
             padding: 0px 0px 6px 0px;
             font-size: 51px;
             line-height: 44px;
             letter-spacing: -2px;
             font-weight: bold;
        }
        #footer {
            position:absolute;
            bottom:10px;
            right: 10px;
        }
        #footer button {
            background-color:#006299;
            color:#fff;
            border:1px solid #006299;
            border-radius:5px;
            padding: 5px 10px;
        }
        #footer button:hover {
            background-color:#368fe0;
            color:#000;
        }
        #pack {width: 400px;}
        #paragraph {position: absolute;
                    bottom: 10px;
                    left: 10px;
                    font-family:Georgia,serif;
                    color:#4E443C;
                    font-variant: small-caps; 
                    text-transform: none; 
                    font-weight: 100; }
        #gotoI {border-radius: 4px;
                width: 75px;
        }
        #centerFooter {position: absolute;
                       bottom: 10px;
                       left: 50%;}
        #centerFooter button {background-color:#006299;
                              color:#fff;
                              border:1px solid #006299;
                              border-radius:5px;
                              padding: 5px 10px;}
        #centerFooter button:hover {
            background-color:#368fe0;
            color:#000;
        }
        #sentenceN {
            font-family:Georgia,serif;
            color:#4E443C;
            font-variant: small-caps; 
            text-transform: none; 
            font-weight: 100; 

        }
    </style>
</html>

