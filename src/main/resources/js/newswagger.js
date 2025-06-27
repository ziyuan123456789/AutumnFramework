const {useState, useEffect, useMemo, useCallBack} = Dong;
const EndpointPanel = ({endpoint, interactiveState, onParamChange, onSendRequest}) => {
    const {handlerInfo, fullPath} = endpoint;
    const state = interactiveState[fullPath] || {parameterValues: {}, loading: false, response: null};
    return /* @__PURE__ */ Dong.createElement("div", {className: "api-details"}, /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
        "span",
        {
            className: "detail-item-label"
        },
        "Bean Name:"
    ), /* @__PURE__ */ Dong.createElement("span", null, handlerInfo.beanName)), /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
        "span",
        {
            className: "detail-item-label"
        },
        "Method Name:"
    ), /* @__PURE__ */ Dong.createElement("span", null, handlerInfo.methodName)), /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
        "span",
        {
            className: "detail-item-label"
        },
        "Return Type:"
    ), /* @__PURE__ */ Dong.createElement("span", null, handlerInfo.returnType)), handlerInfo.parameterNames.length > 0 && /* @__PURE__ */ Dong.createElement("div", {className: "parameter-section"}, /* @__PURE__ */ Dong.createElement("h4", null, "\u53C2\u6570"), handlerInfo.parameterNames.map((paramName, pIndex) => /* @__PURE__ */ Dong.createElement("div", {
        key: pIndex,
        className: "param-input-group"
    }, /* @__PURE__ */ Dong.createElement("div", {className: "param-name"}, paramName, " (", handlerInfo.parameterTypes[pIndex], "):"), /* @__PURE__ */ Dong.createElement(
        "input",
        {
            className: "custom-input",
            type: "text",
            placeholder: `\u8BF7\u8F93\u5165 ${paramName}`,
            value: state.parameterValues[paramName] || "",
            onChange: (e) => onParamChange(fullPath, paramName, e.target.value)
        }
    )))), state.response && /* @__PURE__ */ Dong.createElement("div", {className: "response-card"}, /* @__PURE__ */ Dong.createElement("div", {className: "response-header"}, /* @__PURE__ */ Dong.createElement("span", null, "Response"), /* @__PURE__ */ Dong.createElement(
        "span",
        {
            className: `status-tag ${state.response.status >= 200 && state.response.status < 300 ? "status-success" : "status-error"}`
        },
        "Status: ",
        state.response.status
    )), /* @__PURE__ */ Dong.createElement("pre", {className: "response-body"}, state.response.body)));
};
const App = () => {
    const [loading, setLoading] = useState(true);
    const [endpoints, setEndpoints] = useState([]);
    const [interactiveState, setInteractiveState] = useState({});
    const [activeKey, setActiveKey] = useState(null);
    const handleToggle = useCallBack((key) => {
        setActiveKey((prevKey) => prevKey === key ? null : key);
    }, []);
    useEffect(() => {
        const fetchApiData = async () => {
            setLoading(true);
            try {
                const response = await fetch("/urlMapping");
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                const flattenEndpoints = (node, path) => {
                    let results = [];
                    if (node && node.handlerInfo && node.endpoint) {
                        results.push({fullPath: path, handlerInfo: node.handlerInfo});
                    }
                    if (node && node.staticChildren) {
                        for (const key in node.staticChildren) {
                            results = results.concat(flattenEndpoints(node.staticChildren[key], path + "/" + key));
                        }
                    }
                    if (node && node.paramChild) {
                        results = results.concat(flattenEndpoints(node.paramChild, path + "/{" + node.paramChild.paramName + "}"));
                    }
                    return results;
                };
                const flattened = flattenEndpoints(data.root || data, "");
                setEndpoints(flattened);
                setInteractiveState(flattened.reduce((acc, ep) => {
                    acc[ep.fullPath] = {parameterValues: {}, loading: false, response: null};
                    return acc;
                }, {}));
            } catch (error) {
                console.error("Failed to fetch API data:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchApiData();
    }, []);
    const groupedEndpoints = useMemo(() => {
        const groups = {};
        endpoints.forEach((endpoint) => {
            const parts = endpoint.fullPath.split("/");
            const groupName = parts[1] || "root";
            if (!groups[groupName]) {
                groups[groupName] = [];
            }
            groups[groupName].push(endpoint);
        });
        return groups;
    }, [endpoints]);
    const handleParamChange = useCallBack((fullPath, paramName, value) => {
        setInteractiveState((prev) => ({
            ...prev,
            [fullPath]: {...prev[fullPath], parameterValues: {...prev[fullPath].parameterValues, [paramName]: value}}
        }));
    }, []);
    const sendRequest = useCallBack(async (endpoint) => {
        const fullPath = endpoint.fullPath;
        const currentState = interactiveState[fullPath];
        setInteractiveState((prev) => ({...prev, [fullPath]: {...currentState, loading: true, response: null}}));
        try {
            const params = new URLSearchParams(currentState.parameterValues).toString();
            console.log(`Sending request to: ${fullPath}?${params}`);
            await new Promise((resolve) => setTimeout(resolve, 1e3));
            const mockResponse = {
                status: 200,
                body: JSON.stringify({
                    message: `Successfully called ${endpoint.handlerInfo.methodName}`,
                    params_received: currentState.parameterValues
                }, null, 2)
            };
            setInteractiveState((prev) => ({
                ...prev,
                [fullPath]: {...currentState, response: mockResponse, loading: false}
            }));
        } catch (error) {
            const errorResponse = {
                status: 500,
                body: JSON.stringify({error: "Failed to fetch", message: error.message}, null, 2)
            };
            setInteractiveState((prev) => ({
                ...prev,
                [fullPath]: {...currentState, response: errorResponse, loading: false}
            }));
        }
    }, [interactiveState]);
    const getMethodClass = (methodName) => typeof methodName === "string" ? methodName.toLowerCase().startsWith("post") || methodName.toLowerCase().startsWith("create") ? "method-post" : methodName.toLowerCase().startsWith("put") || methodName.toLowerCase().startsWith("update") ? "method-put" : methodName.toLowerCase().startsWith("delete") || methodName.toLowerCase().startsWith("remove") ? "method-delete" : "method-get" : "method-get";
    const guessMethod = (methodName) => typeof methodName === "string" ? methodName.toLowerCase().startsWith("post") || methodName.toLowerCase().startsWith("create") ? "POST" : methodName.toLowerCase().startsWith("put") || methodName.toLowerCase().startsWith("update") ? "PUT" : methodName.toLowerCase().startsWith("delete") || methodName.toLowerCase().startsWith("remove") ? "DELETE" : "GET" : "GET";
    return /* @__PURE__ */ Dong.createElement("div", {className: "layout"}, /* @__PURE__ */ Dong.createElement("header", {className: "header"}, /* @__PURE__ */ Dong.createElement("h1", {className: "header-title"}, "AutumnFramework With MineReact"), /* @__PURE__ */ Dong.createElement("h3", null, "\u70B9\u51FB\u8FDB\u5165MineReact\u4ECB\u7ECD\u9875")), loading ? /* @__PURE__ */ Dong.createElement("div", {className: "loading-spinner"}, "\u6B63\u5728\u52A0\u8F7DAPI\u5217\u8868...") : /* @__PURE__ */ Dong.createElement("main", {className: "content"}, Object.entries(groupedEndpoints).map(([groupName, endpointsInGroup]) => /* @__PURE__ */ Dong.createElement("div", {
        key: groupName,
        className: "api-group"
    }, /* @__PURE__ */ Dong.createElement("h2", {className: "group-title"}, groupName.toUpperCase(), " \u7AEF\u70B9"), /* @__PURE__ */ Dong.createElement("div", {className: "collapse"}, endpointsInGroup.map((endpoint) => {
        const isActive = activeKey === endpoint.fullPath;
        return /* @__PURE__ */ Dong.createElement("div", {
            key: endpoint.fullPath,
            className: "collapse-item"
        }, /* @__PURE__ */ Dong.createElement(
            "div",
            {
                className: "collapse-header",
                onClick: () => handleToggle(endpoint.fullPath)
            },
            /* @__PURE__ */ Dong.createElement(
                "span",
                {
                    className: `http-method ${getMethodClass(endpoint.handlerInfo.methodName)}`
                },
                guessMethod(endpoint.handlerInfo.methodName)
            ),
            /* @__PURE__ */ Dong.createElement("span", {className: "endpoint-path"}, endpoint.fullPath),
            /* @__PURE__ */ Dong.createElement(
                "span",
                {
                    className: "endpoint-summary"
                },
                endpoint.handlerInfo.methodName
            )
        ), isActive && /* @__PURE__ */ Dong.createElement("div", {className: "collapse-content"}, /* @__PURE__ */ Dong.createElement(
            EndpointPanel,
            {
                endpoint,
                interactiveState,
                onParamChange: handleParamChange,
                onSendRequest: sendRequest
            }
        )));
    }))))));
};
const root = document.getElementById("root");
if (root) {
    Dong.render(/* @__PURE__ */ Dong.createElement(App, null), root);
}
