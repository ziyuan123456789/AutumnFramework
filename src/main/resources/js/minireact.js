var Dong = (() => {
  var __defProp = Object.defineProperty;
  var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __hasOwnProp = Object.prototype.hasOwnProperty;
  var __export = (target, all) => {
    for (var name in all)
      __defProp(target, name, {get: all[name], enumerable: true});
  };
  var __copyProps = (to, from, except, desc) => {
    if (from && typeof from === "object" || typeof from === "function") {
      for (let key of __getOwnPropNames(from))
        if (!__hasOwnProp.call(to, key) && key !== except)
          __defProp(to, key, {
            get: () => from[key],
            enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable
          });
    }
    return to;
  };
  var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", {value: true}), mod);

  // src/dong.ts
  var dong_exports = {};
  __export(dong_exports, {
    default: () => dong_default,
    render: () => render,
    useAware: () => useAware,
    useCallBack: () => useCallBack,
    useEffect: () => useEffect,
    useMemo: () => useMemo,
    useRef: () => useRef,
    useState: () => useState
  });

  function createElement(type, props, ...children) {
    console.log("createElement\u751F\u6210\u7684\u539F\u59CB\u865A\u62DFDOM\u5982\u4E0B");
    if (type == void 0) {
      console.error("ERROR");
    }
    console.log({
      type,
      props: {
        ...props,
        children: children.map(
            (child) => typeof child === "object" ? child : createTextElement(child)
        )
      }
    });
    return {
      type,
      props: {
        ...props,
        children: children.map(
            (child) => typeof child === "object" ? child : createTextElement(child)
        )
      }
    };
  }

  function createTextElement(text) {
    return {
      type: "TEXT_ELEMENT",
      props: {
        nodeValue: text,
        children: []
      }
    };
  }

  var nextFiberReconcileWork = null;
  var wipRoot = null;
  var currentRoot = null;
  var deletions = [];
  var currentFiber = null;
  var hookIndex = 0;

  function render(element, container) {
    wipRoot = {
      dom: container,
      //渲染的目标容器
      props: {
        children: [element]
        //虚拟dom
      },
      alternate: currentRoot
      //当前根Fiber树存入wipRoot.alternate
    };
    nextFiberReconcileWork = wipRoot;
    console.log("render\u9636\u6BB5\u7ED3\u675F,\u751F\u6210\u7684wipRoot\u5982\u4E0B");
    console.log(wipRoot);
  }

  requestIdleCallback(workLoop);

  function workLoop(deadline) {
    let shouldYield = false;
    while (nextFiberReconcileWork && !shouldYield) {
      nextFiberReconcileWork = performNextWork(nextFiberReconcileWork);
      shouldYield = deadline.timeRemaining() < 1;
      if (shouldYield) {
        console.warn("\u7A7A\u95F2\u65F6\u95F4\u8017\u5C3D\uFF0C\u751F\u6210\u865A\u62DF DOM \u88AB\u6253\u65AD\uFF0C\u7B49\u5F85\u4E0B\u6B21\u8C03\u5EA6\u4EE5\u4FBF\u4ECE\u4E0A\u6B21\u4E2D\u65AD\u7684\u5730\u65B9\u7EE7\u7EED");
      }
    }
    if (nextFiberReconcileWork) {
      requestIdleCallback(workLoop);
    }
    if (!nextFiberReconcileWork && wipRoot) {
      commitRoot();
    }
  }

  function performNextWork(fiber) {
    currentFiber = fiber;
    if (typeof fiber.type === "function") {
      hookIndex = 0;
      fiber.hooks = [];
    }
    reconcile(fiber);
    if (fiber.child) {
      return fiber.child;
    }
    let nextFiber = fiber;
    while (nextFiber) {
      if (nextFiber.sibling) {
        return nextFiber.sibling;
      }
      nextFiber = nextFiber.return;
    }
    return null;
  }

  function reconcile(fiber) {
    var _a;
    if (typeof fiber.type === "function") {
      const child = fiber.type(fiber.props);
      if (Array.isArray(child)) {
        reconcileChildren(fiber, child);
      } else {
        reconcileChildren(fiber, [child]);
      }
    } else {
      if (!fiber.dom) {
        fiber.dom = createDom(fiber);
      }
      const children = ((_a = fiber.props) == null ? void 0 : _a.children) || [];
      reconcileChildren(fiber, children);
    }
  }

  function shallowEqual(obj1, obj2) {
    if (obj1 === obj2) return true;
    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);
    if (keys1.length !== keys2.length) return false;
    for (let key of keys1) {
      const value1 = obj1[key];
      const value2 = obj2[key];
      if (key === "children") {
        if (Array.isArray(value1) && Array.isArray(value2)) {
          if (value1.length !== value2.length) return false;
          if (value1.length > 1) {
            for (let i = 0; i < value1.length; i++) {
              if (value1[i].type !== value2[i].type) {
                return false;
              }
            }
          } else if (value1.length === 1) {
            if (Array.isArray(value1[0])) {
              console.error(value1[0]);
            }
            if (!Array.isArray(value1[0])) {
              return value1[0].props.nodeValue === value2[0].props.nodeValue;
            }
          }
        }
      } else {
        if (key.startsWith("on") && typeof value1 === "function" && typeof value2 === "function") {
          return value1 === value2;
        }
        if (!deepEqual(value1, value2)) {
          return false;
        }
      }
    }
    return true;
  }

  var deepEqual = (obj1, obj2) => {
    if (obj1 === obj2) return true;
    if (typeof obj1 !== "object" || typeof obj2 !== "object" || obj1 == null || obj2 == null) {
      return false;
    }
    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);
    if (keys1.length !== keys2.length) return false;
    for (let key of keys1) {
      if (!keys2.includes(key) || !deepEqual(obj1[key], obj2[key])) {
        return false;
      }
    }
    return true;
  };

  function diff(oldfiber, newfiber) {
    if (oldfiber === newfiber) return true;
    if (oldfiber.type !== newfiber.type) return false;
    return shallowEqual(oldfiber.props, newfiber.props);
  }

  function reconcileChildren(wipFiber, elements) {
    let index = 0;
    let prevSibling = null;
    let oldFiber = wipFiber.alternate && wipFiber.alternate.child;
    let flattenedElements = elements.flat();
    while (index < flattenedElements.length || oldFiber != null) {
      let element = flattenedElements[index];
      if (typeof element !== "object") {
        element = createTextElement(element);
      }
      let newFiber = null;
      const sameType = oldFiber && element && element.type === oldFiber.type;
      if (sameType) {
        const shouldUpdate = !diff(element, oldFiber);
        if (shouldUpdate) {
          console.warn("\u8282\u70B9\u4E0E\u4E0A\u4E00\u8BFEfiber\u6811\u4E0D\u4E00\u81F4,\u9700\u8981\u8FDB\u884C\u8282\u70B9\u66F4\u65B0,\u66F4\u65B0\u7684fiber\u5982\u4E0B");
          newFiber = {
            type: oldFiber.type,
            props: element.props,
            dom: oldFiber.dom,
            return: wipFiber,
            alternate: oldFiber,
            effectTag: "UPDATE"
          };
          console.log(newFiber);
          console.log(oldFiber);
        } else {
          newFiber = {
            type: oldFiber.type,
            props: element.props,
            dom: oldFiber.dom,
            return: wipFiber,
            alternate: oldFiber,
            effectTag: ""
          };
        }
      } else {
        if (element) {
          newFiber = {
            type: element.type,
            props: element.props,
            dom: null,
            return: wipFiber,
            alternate: null,
            effectTag: "PLACEMENT"
          };
        }
        if (oldFiber) {
          oldFiber.effectTag = "DELETION";
          deletions.push(oldFiber);
        }
      }
      if (oldFiber) {
        oldFiber = oldFiber.sibling;
      }
      if (index === 0) {
        wipFiber.child = newFiber;
      } else if (prevSibling) {
        prevSibling.sibling = newFiber;
      }
      console.log("\u534F\u8C03\u540E\u7684\u865A\u62DFDOM\u5982\u4E0B");
      prevSibling = newFiber;
      console.log(prevSibling);
      index++;
    }
  }

  function commitRoot() {
    deletions.forEach(commitWork);
    deletions = [];
    commitWork(wipRoot.child);
    runEffects(wipRoot.child);
    currentRoot = wipRoot;
    wipRoot = null;
  }

  function runEffects(fiber) {
    if (!fiber) return;
    if (fiber.hooks) {
      fiber.hooks.forEach((hook) => {
        if (hook.effect && hook.hasEffect) {
          hook.cleanup = hook.effect();
          hook.hasEffect = false;
        }
      });
    }
    runEffects(fiber.child);
    runEffects(fiber.sibling);
  }

  function commitWork(fiber) {
    if (!fiber) {
      return;
    }
    let domParentFiber = fiber.return;
    while (!domParentFiber.dom) {
      domParentFiber = domParentFiber.return;
    }
    const domParent = domParentFiber.dom;
    if (fiber.effectTag === "PLACEMENT" && fiber.dom != null) {
      domParent.appendChild(fiber.dom);
      domParent.classList.add("fade-in-border");
      setTimeout(() => {
        domParent.classList.remove("fade-in-border");
      }, 3e3);
      console.log("\u8282\u70B9\u63D2\u5165");
    } else if (fiber.effectTag === "UPDATE" && fiber.dom != null) {
      updateDom(fiber.dom, fiber.alternate.props, fiber.props);
    } else if (fiber.effectTag === "DELETION") {
      commitDeletion(fiber, domParent);
      console.log("\u8282\u70B9\u5220\u9664");
      return;
    }
    commitWork(fiber.child);
    commitWork(fiber.sibling);
  }

  function commitDeletion(fiber, domParent) {
    if (!fiber) return;
    if (fiber.dom) {
      domParent.removeChild(fiber.dom);
    } else {
      commitDeletion(fiber.child, domParent);
    }
    commitDeletion(fiber.sibling, domParent);
  }

  function createDom(fiber) {
    let dom;
    if (fiber.type == "TEXT_ELEMENT") {
      dom = document.createTextNode(fiber.props.nodeValue);
    } else {
      dom = document.createElement(fiber.type == void 0 ? "div" : fiber.type);
    }
    for (const prop in fiber.props) {
      setAttribute(dom, prop, fiber.props[prop]);
    }
    console.log("\u771F\u5B9EDOM\u5982\u4E0B:");
    console.log(dom);
    return dom;
  }

  function isEventListenerAttr(key, value) {
    return typeof value == "function" && key.startsWith("on");
  }

  function isStyleAttr(key, value) {
    return key == "style" && typeof value == "object";
  }

  function isPlainAttr(_key, value) {
    return typeof value != "object" && typeof value != "function";
  }

  var setAttribute = (dom, key, value) => {
    if (key === "children") {
      return;
    }
    if (key === "nodeValue") {
      dom.textContent = value;
    } else if (isEventListenerAttr(key, value)) {
      const eventType = key.slice(2).toLowerCase();
      dom.addEventListener(eventType, value);
    } else if (isStyleAttr(key, value)) {
      Object.assign(dom.style, value);
    } else if (key === "ref" && typeof value === "object") {
      value.current = dom;
    } else if (isPlainAttr(key, value)) {
      dom.setAttribute(key, value);
    }
  };

  function updateDom(dom, prevProps, nextProps) {
    if (dom instanceof Text) {
      if (prevProps.nodeValue !== nextProps.nodeValue) {
        dom.nodeValue = nextProps.nodeValue;
      }
      return;
    }
    Object.entries(prevProps).filter(([key, value]) => isEventListenerAttr(key, value)).forEach(([name, value]) => {
      const eventType = name.toLowerCase().substring(2);
      dom.removeEventListener(eventType, value);
    });
    Object.entries(nextProps).filter(([key, value]) => isEventListenerAttr(key, value)).forEach(([name, value]) => {
      const eventType = name.toLowerCase().substring(2);
      dom.addEventListener(eventType, value);
    });
    Object.keys(prevProps).filter((key) => isPlainAttr(key, prevProps[key])).forEach((name) => {
      if (!(name in nextProps)) {
        dom.removeAttribute(name);
      }
    });
    Object.keys(nextProps).filter((key) => isPlainAttr(key, nextProps[key])).forEach((name) => {
      if (prevProps[name] !== nextProps[name]) {
        dom.setAttribute(name, nextProps[name]);
      }
    });
    if (prevProps.style) {
      Object.keys(prevProps.style).forEach((key) => {
        if (!nextProps.style || !(key in nextProps.style)) {
          dom.style[key] = "";
        }
      });
    }
    if (nextProps.style) {
      Object.keys(nextProps.style).forEach((key) => {
        if (!prevProps.style || prevProps.style[key] !== nextProps.style[key]) {
          dom.style[key] = nextProps.style[key];
        }
      });
    }
    dom.classList.add("fade-in-border");
    setTimeout(() => {
      dom.classList.remove("fade-in-border");
    }, 3e3);
  }

  function useState(initialValue) {
    const oldHook = currentFiber.alternate && currentFiber.alternate.hooks ? currentFiber.alternate.hooks[hookIndex] : null;
    const hook = {
      state: oldHook ? oldHook.state : initialValue,
      queue: oldHook ? oldHook.queue : []
    };
    hook.queue.forEach((action) => {
      console.log("\u5904\u7406hooks\u4E2D");
      console.log("action", action);
      hook.state = action(hook.state);
    });
    hook.queue.length = 0;
    const setState = (action) => {
      console.log("setState\u8C03\u7528");
      hook.queue.push(typeof action === "function" ? action : () => action);
      wipRoot = {
        dom: currentRoot.dom,
        props: currentRoot.props,
        alternate: currentRoot
      };
      nextFiberReconcileWork = wipRoot;
      console.log("\u8C03\u7528useState\u9020\u6210\u91CD\u65B0\u6E32\u67D3");
      while (nextFiberReconcileWork) {
        nextFiberReconcileWork = performNextWork(nextFiberReconcileWork);
      }
      if (!nextFiberReconcileWork && wipRoot) {
        commitRoot();
      }
    };
    currentFiber.hooks.push(hook);
    hookIndex++;
    return [hook.state, setState];
  }

  function useEffect(callback, deps) {
    const oldHook = currentFiber.alternate && currentFiber.alternate.hooks ? currentFiber.alternate.hooks[hookIndex] : null;
    let hasChangedDeps;
    if (!oldHook) {
      hasChangedDeps = true;
    } else {
      if (deps) {
        hasChangedDeps = deps.some((dep, i) => {
          return !Object.is(dep, oldHook.deps[i]);
        });
      } else {
        hasChangedDeps = true;
      }
    }
    const hook = {
      deps,
      // 当前的依赖项
      effect: callback,
      // 副作用函数
      cleanup: oldHook ? oldHook.cleanup : null
      // 保存旧的清理函数
    };
    if (hasChangedDeps) {
      if (hook.cleanup) {
        hook.cleanup();
      }
      const cleanup = callback();
      hook.cleanup = typeof cleanup === "function" ? cleanup : null;
    }
    currentFiber.hooks.push(hook);
    hookIndex++;
  }

  function useMemo(callback, deps) {
    const oldHook = currentFiber.alternate && currentFiber.alternate.hooks ? currentFiber.alternate.hooks[hookIndex] : null;
    let hasChangedDeps = false;
    let memoizedValue;
    if (!oldHook) {
      hasChangedDeps = true;
    } else {
      if (deps) {
        hasChangedDeps = deps.some((dep, i) => !Object.is(dep, oldHook.deps[i]));
      } else {
        hasChangedDeps = true;
      }
    }
    if (hasChangedDeps) {
      memoizedValue = callback();
    } else {
      memoizedValue = oldHook.memoizedValue;
    }
    const hook = {
      memoizedValue,
      deps
    };
    currentFiber.hooks.push(hook);
    hookIndex++;
    return memoizedValue;
  }

  function useCallBack(callback, deps) {
    const oldHook = currentFiber.alternate && currentFiber.alternate.hooks ? currentFiber.alternate.hooks[hookIndex] : null;
    let hasChangedDeps;
    if (!oldHook) {
      hasChangedDeps = true;
    } else {
      if (deps) {
        hasChangedDeps = deps.some((dep, i) => !Object.is(dep, oldHook.deps[i]));
      } else {
        hasChangedDeps = true;
      }
    }
    const hook = {
      callback: hasChangedDeps ? callback : oldHook.callback,
      deps
    };
    currentFiber.hooks.push(hook);
    hookIndex++;
    return hook.callback;
  }

  function useAware() {
    const seen = /* @__PURE__ */ new Set();

    function replacer(key, value) {
      if (key === "dom" || key === "alternate") {
        return "[\u5FFD\u7565]";
      }
      if (key === "props" && typeof value === "object" && value !== null) {
        const filteredProps = {};
        Object.keys(value).forEach((propKey) => {
          if (propKey === "children" || typeof value[propKey] !== "function") {
            filteredProps[propKey] = value[propKey];
          }
        });
        return filteredProps;
      }
      if (typeof value === "object" && value !== null) {
        if (seen.has(value)) {
          return "[\u5FAA\u73AF\u5F15\u7528]";
        }
        seen.add(value);
      }
      return value;
    }

    return [JSON.stringify(wipRoot, replacer, 2), wipRoot];
  }

  function useRef(initialValue) {
    const oldHook = currentFiber.alternate && currentFiber.alternate.hooks ? currentFiber.alternate.hooks[hookIndex] : null;
    const hook = oldHook || {current: initialValue};
    currentFiber.hooks.push(hook);
    hookIndex++;
    return hook;
  }

  var Dong = {
    createElement,
    render,
    useState,
    useEffect,
    useAware,
    useCallBack,
    useRef,
    useMemo
  };
  if (typeof window !== "undefined") {
    window.Dong = Dong;
  }
  var dong_default = Dong;
  return __toCommonJS(dong_exports);
})();
