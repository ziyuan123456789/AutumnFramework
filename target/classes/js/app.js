//Babel转化后代码如下:
function Square({value, onSquareClick}) {
    return /* @__PURE__ */ Dong.createElement(
        "div",
        {
            style: {
                width: "50px",
                height: "50px",
                backgroundColor: "#f4f4f4",
                border: "1px solid #ccc",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                fontSize: "24px",
                cursor: "pointer",
                transition: "background-color 0.3s ease"
            },
            onClick: onSquareClick
        },
        value
    );
}

const Board = ({xIsNext, squares, onPlay}) => {
    function handleClick(i) {
        if (calculateWinner(squares) || squares[i]) {
            return;
        }
        const nextSquares = squares.slice();
        if (xIsNext) {
            nextSquares[i] = "X";
        } else {
            nextSquares[i] = "O";
        }
        onPlay(nextSquares);
    }

    const winner = calculateWinner(squares);
    let status;
    if (winner) {
        status = "Winner: " + winner;
    } else {
        status = "Next player: " + (xIsNext ? "X" : "O");
    }
    return /* @__PURE__ */ Dong.createElement("div", {
        style: {
            display: "flex",
            flexDirection: "column",
            marginBottom: "20px"
        }
    }, /* @__PURE__ */ Dong.createElement("div", {
        style: {
            fontSize: "20px",
            marginBottom: "20px"
        }
    }, status), /* @__PURE__ */ Dong.createElement("div", {
        style: {
            display: "flex",
            justifyContent: "center",
            marginBottom: "5px"
        }
    }, /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[0],
        onSquareClick: () => handleClick(0)
    }), /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[1],
        onSquareClick: () => handleClick(1)
    }), /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[2],
        onSquareClick: () => handleClick(2)
    })), /* @__PURE__ */ Dong.createElement("div", {
        style: {
            display: "flex",
            justifyContent: "center",
            marginBottom: "5px"
        }
    }, /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[3],
        onSquareClick: () => handleClick(3)
    }), /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[4],
        onSquareClick: () => handleClick(4)
    }), /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[5],
        onSquareClick: () => handleClick(5)
    })), /* @__PURE__ */ Dong.createElement("div", {
        style: {
            display: "flex",
            justifyContent: "center"
        }
    }, /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[6],
        onSquareClick: () => handleClick(6)
    }), /* @__PURE__ */ Dong.createElement(Square, {
        value: squares[7],
        onSquareClick: () => handleClick(7)
    }), /* @__PURE__ */ Dong.createElement(Square, {value: squares[8], onSquareClick: () => handleClick(8)})));
};

function calculateWinner(squares) {
    const lines = [
        [0, 1, 2],
        [3, 4, 5],
        [6, 7, 8],
        [0, 3, 6],
        [1, 4, 7],
        [2, 5, 8],
        [0, 4, 8],
        [2, 4, 6]
    ];
    for (let i = 0; i < lines.length; i++) {
        const [a, b, c] = lines[i];
        if (squares[a] && squares[a] === squares[b] && squares[a] === squares[c]) {
            return squares[a];
        }
    }
    return null;
}

const Parent = () => {
    const [num, setNum] = Dong.useState(0);
    const computeResult = Dong.useMemo(() => {
        console.error("useMemo\u8BA1\u7B97");
        return /* @__PURE__ */ Dong.createElement("h4", null, num);
    }, [num]);
    return /* @__PURE__ */ Dong.createElement("div", null, computeResult, /* @__PURE__ */ Dong.createElement(
        "h5",
        {
            onClick: () => {
                setNum(num + 1);
            }
        },
        "useMemo\u6D4B\u8BD5"
    ));
};

function App() {
    const divRef = Dong.useRef(null);
    const [position, setPosition] = Dong.useState({
        x: 0,
        y: 0
    });
    const [history, setHistory] = Dong.useState([Array(9).fill(null)]);
    const [currentMove, setCurrentMove] = Dong.useState(0);
    const xIsNext = currentMove % 2 === 0;
    const currentSquares = history[currentMove];
    const [data, setData] = Dong.useState(114514);
    const [backgroundColor, setBackgroundColor] = Dong.useState("");
    const [xData, setXData] = Dong.useState([]);
    const [yData, setYData] = Dong.useState([]);
    const [vDomString] = Dong.useAware();
    const inputRef = Dong.useRef(null);
    const chartRef = Dong.useRef(null);
    const handlePointerMove = Dong.useCallBack((e) => {
        const divRect = divRef.current.getBoundingClientRect();
        const x = e.clientX - divRect.left;
        const y = e.clientY - divRect.top;
        setPosition({x, y});
    }, []);
    const handlePlay = Dong.useCallBack((nextSquares) => {
        const nextHistory = [...history.slice(0, currentMove + 1), nextSquares];
        setHistory(nextHistory);
        setCurrentMove(nextHistory.length - 1);
    }, [history, currentMove]);
    const handleRef = Dong.useCallBack(() => {
        alert(inputRef.current?.value);
    }, []);
    Dong.useEffect(() => {
        const realDomContainer2 = document.getElementById("realdom");
        if (realDomContainer2) {
            realDomContainer2.innerHTML = `
        <h1>\u865A\u62DF DOM \u5C55\u793A</h1>
        <h5>\u6765\u81EAuseEffect\u7684\u6D88\u606F:\u8FD9\u662F\u4E00\u4E2A\u7A7A\u6570\u7EC4\u4F9D\u8D56useEffect, \u9875\u9762\u52A0\u8F7D\u4F1A\u8FD0\u884C\u4E00\u6B21</h5>
        <div>\u8FD9\u6BB5\u5185\u5BB9\u5DF2\u8131\u79BB\u865A\u62DFDOM\u7BA1\u7406,MiniReact\u65E0\u6CD5\u611F\u77E5\u5230\u8FD9\u90E8\u5206\u7684\u53D8\u5316</div>
        <pre>${Dong.useAware()[0]}</pre>`;
        }
        return () => {
            console.log("\u6E05\u7406\u526F\u4F5C\u7528");
        };
    }, []);
    const testFunction = () => {
        console.log("\u611A\u8822\u7684\u7684MiniReact\u5E76\u4E0D\u77E5\u9053\u51FD\u6570\u5230\u5E95\u53D8\u4E86\u6CA1");
    };
    const testFunctionWithUseCallBack = Dong.useCallBack(() => {
        console.log("\u611A\u8822\u7684\u7684MiniReact\u8FD8\u662F\u5E76\u4E0D\u77E5\u9053\u51FD\u6570\u5230\u5E95\u53D8\u4E86\u6CA1,\u6240\u4EE5\u4ED6\u6253\u7B97\u5F15\u5165\u4E00\u4E9B\u5916\u63F4");
    }, []);
    const [functionHandler] = Dong.useState(testFunction);
    const [functionHandlerWithUseCallBack] = Dong.useState(testFunctionWithUseCallBack);
    Dong.useEffect(() => {
        if (testFunction === functionHandler) {
            console.log("\u7B2C\u4E00\u6B21\u8FD0\u884C,\u6240\u4EE5\u5F15\u7528\u76F8\u540C");
        } else {
            console.log("\u4EC0\u4E48\u4E8B\u60C5\u90FD\u662F\u7B2C\u4E00\u6B21\u597D,\u7B2C\u4E8C\u6B21\u5C31\u4E0D\u662F\u4E00\u4E2A\u611F\u89C9\u4E86");
        }
    });
    Dong.useEffect(() => {
        if (testFunctionWithUseCallBack === functionHandlerWithUseCallBack) {
            console.log("useCallBack\u751F\u6548\u4E2D");
        } else {
            console.log("useCallBack\u5931\u6548\u4E86");
        }
    });
    Dong.useEffect(() => {
        const realDomContainer2 = document.getElementById("realdom");
        if (realDomContainer2) {
            realDomContainer2.innerHTML = `
        <h2>\u865A\u62DF DOM \u5C55\u793A</h2>
        <div>\u8FD9\u6BB5\u5185\u5BB9\u5DF2\u8131\u79BB\u865A\u62DFDOM\u7BA1\u7406,MiniReact\u65E0\u6CD5\u611F\u77E5\u5230\u8FD9\u90E8\u5206\u7684\u53D8\u5316</div>
        <pre>${Dong.useAware()[0]}</pre>
    `;
        }
    }, [vDomString]);
    Dong.useEffect(() => {
        const realDomContainer2 = document.getElementById("realdom");
        if (realDomContainer2) {
            realDomContainer2.innerHTML = `
        <h1>\u865A\u62DF DOM \u5C55\u793A</h1>
        <h5>\u6765\u81EAuseEffect\u7684\u6D88\u606F:\u8FD9\u4E2AuseEffect\u4F9D\u8D56\u4E8Edata, \u9875\u9762\u52A0\u8F7D\u4F1A\u8FD0\u884C\u4E00\u6B21, data\u53D8\u52A8\u65F6\u4E5F\u4F1A\u89E6\u53D1\uFF0C\u5F53\u524D\u503C\u4E3A ${data}</h5>
        <div>\u8FD9\u6BB5\u5185\u5BB9\u5DF2\u8131\u79BB\u865A\u62DFDOM\u7BA1\u7406,MiniReact\u65E0\u6CD5\u611F\u77E5\u5230\u8FD9\u90E8\u5206\u7684\u53D8\u5316</div>
        <pre>${Dong.useAware()[0]}</pre>`;
        }
        return () => {
            console.log("\u6E05\u7406\u526F\u4F5C\u7528");
        };
    }, [data]);
    const generateRandomColor = Dong.useCallBack(() => {
        const letters = "0123456789ABCDEF";
        let color = "#";
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }, []);
    const handleClick = Dong.useCallBack(() => {
        setData((temp) => temp + 1);
        setBackgroundColor(generateRandomColor());
    }, [generateRandomColor]);
    Dong.useEffect(() => {
        setTimeout(() => {
            const chartDom = chartRef.current;
            if (!chartDom) return;
            const myChart = echarts.init(chartDom);
            setXData(() => [...xData, 1]);
            setYData(() => [...xData, 1]);
            const option = {
                xAxis: {
                    type: "category",
                    data: xData
                },
                yAxis: {
                    type: "value"
                },
                series: [
                    {
                        data: yData,
                        type: "bar"
                    }
                ]
            };
            myChart.setOption(option);
            return () => {
                myChart.dispose();
            };
        }, 0);
    }, [data]);
    return /* @__PURE__ */ Dong.createElement("div", {
        id: "app",
        style: {fontFamily: "sans-serif", padding: "2rem", maxWidth: "960px", margin: "0 auto"}
    }, /* @__PURE__ */ Dong.createElement(
        "h1",
        {
            style: {backgroundColor, transition: "background 0.5s", padding: "1rem", borderRadius: "8px"},
            onClick: handleClick
        },
        "\u{1F3AF} MiniReact - \u70B9\u51FB\u89E6\u53D1\u4E00\u6B21 useState"
    ), /* @__PURE__ */ Dong.createElement("p", {
        style: {
            color: "#888",
            fontSize: "15px"
        }
    }, "\u5F53useState\u9020\u6210\u6570\u636E\u53D8\u52A8\u540E,Diff\u7B97\u6CD5\u4F1A\u627E\u51FA\u66F4\u65B0/\u63D2\u5165\u7684\u8282\u70B9,\u7ED8\u5236\u6DE1\u84DD\u8272\u8FB9\u6846\u4EE5\u63D0\u793A\u7EC4\u4EF6\u53D1\u751F\u4E86\u91CD\u65B0\u6E32\u67D3 \u5982\u679C\u6CA1\u6709\u59A5\u5584\u4F7F\u7528useCallBack/Memo,MiniReact\u4F1A\u56E0\u4E3A\u51FD\u6570\u5730\u5740\u7684\u53D8\u66F4\u8BA4\u4E3A\u7EC4\u4EF6\u53D8\u52A8,\u8FDB\u884C\u91CD\u7ED8"), /* @__PURE__ */ Dong.createElement("section", {style: {marginTop: "2rem"}}, /* @__PURE__ */ Dong.createElement("h2", null, "\u{1F4D8} React \u5B98\u65B9\u6559\u7A0B\u7684\u4E95\u5B57\u68CB\u6E38\u620F"), /* @__PURE__ */ Dong.createElement("div", {
        className: "game",
        style: {display: "flex", justifyContent: "center"}
    }, /* @__PURE__ */ Dong.createElement("div", {className: "game-board"}, /* @__PURE__ */ Dong.createElement(Board, {
        xIsNext,
        squares: currentSquares,
        onPlay: handlePlay
    })))), /* @__PURE__ */ Dong.createElement("section", {style: {marginTop: "2rem"}}, /* @__PURE__ */ Dong.createElement("h2", null, "\u{1F388} \u6765\u8BD5\u4E00\u4E0B\u79FB\u52A8\u5C0F\u7403"), /* @__PURE__ */ Dong.createElement("p", {
        style: {
            color: "#888",
            fontSize: "14px"
        }
    }, "\u5728\u9F20\u6807\u79FB\u52A8\u8FD9\u6837\u7684\u60C5\u51B5\u4E2D\u63A7\u5236\u53F0\u9891\u7E41\u8F93\u51FA\u4F1A\u9020\u6210\u6389\u5E27,\u5EFA\u8BAE\u5173\u95ED Console \u63D0\u5347\u5E27\u7387"), /* @__PURE__ */ Dong.createElement(
        "div",
        {
            ref: divRef,
            onPointerMove: handlePointerMove,
            style: {
                position: "relative",
                width: "40vw",
                height: "40vh",
                backgroundColor: "#f0f0f0",
                border: "1px solid #ddd",
                borderRadius: "12px",
                overflow: "hidden",
                marginTop: "1rem"
            }
        },
        /* @__PURE__ */ Dong.createElement(
            "div",
            {
                style: {
                    position: "absolute",
                    backgroundColor: "red",
                    borderRadius: "50%",
                    transform: `translate(${position.x - 10}px, ${position.y - 10}px)`,
                    width: "20px",
                    height: "20px",
                    transition: "transform 0.05s linear"
                }
            }
        )
    )), /* @__PURE__ */ Dong.createElement("section", {style: {marginTop: "2rem"}}, /* @__PURE__ */ Dong.createElement("h2", null, "\u{1F9EA} useRef\u4E0EuseCallBack/useMemo\u6D4B\u8BD5"), /* @__PURE__ */ Dong.createElement(
        "input",
        {
            ref: inputRef,
            style: {padding: "8px", border: "1px solid #ccc", borderRadius: "4px", width: "200px"}
        }
    ), /* @__PURE__ */ Dong.createElement("button", {
        onClick: handleRef,
        style: {marginLeft: "1rem"}
    }, "\u70B9\u51FB\u83B7\u53D6\u8F93\u5165\u6846\u5185\u5BB9"), /* @__PURE__ */ Dong.createElement("div", {
        ref: chartRef,
        style: {width: "400px", height: "400px"}
    }), /* @__PURE__ */ Dong.createElement(Parent, null)));
}

function startApp() {
    Dong.render(/* @__PURE__ */ Dong.createElement(App, null), document.querySelector(".container #root"));
    const realDomContainer = document.querySelector(".container #realdom");
    realDomContainer.innerHTML = `
        <h2>\u865A\u62DF DOM \u5C55\u793A</h2>
        <div>\u8FD9\u6BB5\u5185\u5BB9\u5DF2\u8131\u79BB\u865A\u62DFDOM\u7BA1\u7406,MiniReact\u65E0\u6CD5\u611F\u77E5\u5230\u8FD9\u90E8\u5206\u7684\u53D8\u5316</div>
        <pre>${Dong.useAware()[0]}</pre>
    `;
}

document.addEventListener('DOMContentLoaded', () => {
    const root = document.querySelector(".container #root");
    if (root) {
        alert("挂载一个监听器看看虚拟DOM是否渲染成功");
        startApp()
        const checkApp = () => {
            const app = root.querySelector("#app");
            if (app) {
                alert("页面渲染完成");
                clearInterval(interval);
            } else {
                alert("页面渲染异常,重新调用 startApp");
                startApp();
            }
        };
        const interval = setInterval(() => {
            checkApp();
        }, 500);
        setTimeout(() => {
            clearInterval(interval);
        }, 5000);
    } else {
        alert("页面加载失败,即将刷新");
        console.log("当前页面 DOM 结构:", document.body.innerHTML);
        window.location.reload();
    }

    document.title = "MiniReactWithJSX";
});


