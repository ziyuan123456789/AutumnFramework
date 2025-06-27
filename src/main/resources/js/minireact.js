(function (global, factory) {
  typeof exports === "object" && typeof module !== "undefined" ? factory(exports) : typeof define === "function" && define.amd ? define(["exports"], factory) : (global = typeof globalThis !== "undefined" ? globalThis : global || self, factory(global.Dong = {}));
})(this, function (exports2) {
  "use strict";
  function createElement(type, props, ...children) {
    console.log("createElement生成的原始虚拟DOM如下");
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

  let nextFiberReconcileWork = null;
  let wipRoot = null;
  let currentRoot = null;
  let deletions = [];
  let currentFiber = null;
  let hookIndex = 0;
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
    console.log("render阶段结束,生成的wipRoot如下");
    console.log(wipRoot);
  }
  requestIdleCallback(workLoop);
  function workLoop(deadline) {
    let shouldYield = false;
    while (nextFiberReconcileWork && !shouldYield) {
      nextFiberReconcileWork = performNextWork(nextFiberReconcileWork);
      shouldYield = deadline.timeRemaining() < 1;
      if (shouldYield) {
        console.warn("空闲时间耗尽，生成虚拟 DOM 被打断，等待下次调度以便从上次中断的地方继续");
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

  const deepEqual = (obj1, obj2) => {
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
          console.warn("节点与上一课fiber树不一致,需要进行节点更新,更新的fiber如下");
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
      console.log("协调后的虚拟DOM如下");
      prevSibling = newFiber;
      console.log(prevSibling);
      index++;
    }
  }
  function commitRoot() {
    deletions.forEach(commitWork);
    deletions = [];
    commitWork(wipRoot.child);
    currentRoot = wipRoot;
    wipRoot = null;
    runEffects(currentRoot.child);
  }
  function runEffects(fiber) {
    if (!fiber) return;
    if (fiber.hooks) {
      fiber.hooks.forEach((hook) => {
        if (hook.effect && hook.hasEffect) {
          if (hook.cleanup) {
            hook.cleanup();
          }
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
      console.log("节点插入");
    } else if (fiber.effectTag === "UPDATE" && fiber.dom != null) {
      updateDom(fiber.dom, fiber.alternate.props, fiber.props);
    } else if (fiber.effectTag === "DELETION") {
      commitDeletion(fiber, domParent);
      console.log("节点删除");
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
    console.log("真实DOM如下:");
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

  const setAttribute = (dom, key, value) => {
    if (key === "children") {
      return;
    } else if (key === "className") {
      dom.setAttribute("class", value);
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
      console.log("处理hooks中");
      console.log("action", action);
      hook.state = action(hook.state);
    });
    hook.queue.length = 0;
    const setState = (action) => {
      console.log("setState调用");
      hook.queue.push(typeof action === "function" ? action : () => action);
      wipRoot = {
        dom: currentRoot.dom,
        props: currentRoot.props,
        alternate: currentRoot
      };
      nextFiberReconcileWork = wipRoot;
      console.log("调用useState造成重新渲染");
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
      hasChangedDeps = deps ? deps.some((dep, i) => !Object.is(dep, oldHook.deps[i])) : true;
    }
    const hook = {
      deps,
      effect: callback,
      cleanup: oldHook ? oldHook.cleanup : null,
      hasEffect: hasChangedDeps
    };
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
        return "[忽略]";
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
          return "[循环引用]";
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

  const Dong = {
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
  exports2.default = Dong;
  exports2.render = render;
  exports2.useAware = useAware;
  exports2.useCallBack = useCallBack;
  exports2.useEffect = useEffect;
  exports2.useMemo = useMemo;
  exports2.useRef = useRef;
  exports2.useState = useState;
  Object.defineProperties(exports2, {__esModule: {value: true}, [Symbol.toStringTag]: {value: "Module"}});
});
