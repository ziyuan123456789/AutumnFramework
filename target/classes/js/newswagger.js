const {useState, useEffect, useMemo, useCallBack, useAware} = Dong;
const EndpointPanel = ({endpoint, interactiveState, onParamChange}) => {
  const {handlerInfo, fullPath} = endpoint;
  const state = interactiveState[fullPath] || {parameterValues: {}};
  return /* @__PURE__ */ Dong.createElement("div", {className: "api-details"}, /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
      "span",
      {
        className: "detail-item-label"
      },
      "Bean :"
  ), /* @__PURE__ */ Dong.createElement("span", null, handlerInfo.beanName)), /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
      "span",
      {
        className: "detail-item-label"
      },
      "\u8C03\u7528\u65B9\u6CD5 :"
  ), /* @__PURE__ */ Dong.createElement("span", null, handlerInfo.methodName)), /* @__PURE__ */ Dong.createElement("div", {className: "detail-item"}, /* @__PURE__ */ Dong.createElement(
      "span",
      {
        className: "detail-item-label"
      },
      "\u8FD4\u56DE\u503C:"
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
  )))));
};
const App = () => {
  const [loading, setLoading] = useState(true);
  const [endpoints, setEndpoints] = useState([]);
  const [interactiveState, setInteractiveState] = useState({});
  const [activeKey, setActiveKey] = useState(null);
  const [isVdomVisible, setIsVdomVisible] = useState(false);
  const vDomString = useAware()[0];
  const handleToggle = useCallBack((key) => {
    setActiveKey((prevKey) => prevKey === key ? null : key);
  }, []);
  useEffect(() => {
    const modal = document.getElementById("vdom-modal");
    const closeBtn = document.getElementById("modal-close");
    const handleClose = () => {
      setIsVdomVisible(false);
    };
    const handleOverlayClick = (e) => {
      if (e.target === modal) {
        handleClose();
      }
    };
    if (modal && closeBtn) {
      closeBtn.addEventListener("click", handleClose);
      modal.addEventListener("click", handleOverlayClick);
      return () => {
        closeBtn.removeEventListener("click", handleClose);
        modal.removeEventListener("click", handleOverlayClick);
      };
    }
  }, []);
  useEffect(() => {
    const modal = document.getElementById("vdom-modal");
    const vdomContent = document.getElementById("vdom-content");
    if (modal && vdomContent) {
      if (isVdomVisible) {
        vdomContent.textContent = vDomString;
        modal.classList.add("visible");
      } else {
        modal.classList.remove("visible");
      }
    }
  }, [isVdomVisible, vDomString]);
  useEffect(() => {
    const fetchApiData = async () => {
      setLoading(true);
      try {
        const response = await fetch("http://localhost:81/urlMapping");
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
          acc[ep.fullPath] = {parameterValues: {}};
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
  const temp = useCallBack(() => {
    window.location.href = "https://github.com/ziyuan123456789/mini-react";
  }, []);
  const getMethodClass = (methodName) => typeof methodName === "string" ? methodName.toLowerCase().startsWith("post") || methodName.toLowerCase().startsWith("create") ? "method-post" : methodName.toLowerCase().startsWith("put") || methodName.toLowerCase().startsWith("update") ? "method-put" : methodName.toLowerCase().startsWith("delete") || methodName.toLowerCase().startsWith("remove") ? "method-delete" : "method-get" : "method-get";
  const guessMethod = (methodName) => typeof methodName === "string" ? methodName.toLowerCase().startsWith("post") || methodName.toLowerCase().startsWith("create") ? "POST" : methodName.toLowerCase().startsWith("put") || methodName.toLowerCase().startsWith("update") ? "PUT" : methodName.toLowerCase().startsWith("delete") || methodName.toLowerCase().startsWith("remove") ? "DELETE" : "GET" : "GET";
  return /* @__PURE__ */ Dong.createElement("div", {className: "layout"}, /* @__PURE__ */ Dong.createElement("header", {className: "header"}, /* @__PURE__ */ Dong.createElement("h1", {className: "header-title"}, "AutumnFramework"), /* @__PURE__ */ Dong.createElement("h3", {className: "header-subtitle"}, "\u6B64\u9875\u9762\u7531 MiniReact \u6846\u67B6\u6E32\u67D3,\u6253\u5F00F12\u67E5\u770BDiff\u8FC7\u7A0B,\u5DEE\u5F02\u51FA\u73B0\u65F6\u4F1A\u7ED8\u5236\u4E00\u4E2A\u6DE1\u84DD\u8272\u8FB9\u6846\u63D0\u793A\u5143\u7D20\u66F4\u65B0,", /* @__PURE__ */ Dong.createElement(
      "span",
      {
        onClick: temp
      },
      "\u6B64\u5916\u70B9\u51FB\u8DF3\u8F6C\u5230MiniReact\u4ECB\u7ECD\u9875\u9762"
  )), /* @__PURE__ */ Dong.createElement("button", {
    className: "dev-tools-toggle",
    onClick: () => setIsVdomVisible(true)
  }, "\u5C55\u793A\u865A\u62DFDOM")), loading ? /* @__PURE__ */ Dong.createElement("div", {className: "loading-spinner"}, "\u6B63\u5728\u52A0\u8F7DAPI\u5217\u8868...") : /* @__PURE__ */ Dong.createElement("main", {className: "content"}, Object.entries(groupedEndpoints).map(([groupName, endpointsInGroup]) => /* @__PURE__ */ Dong.createElement("div", {
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
          onParamChange: handleParamChange
        }
    )));
  }))))));
};
const root = document.getElementById("root");
if (root) {
  Dong.render(/* @__PURE__ */ Dong.createElement(App, null), root);
}
