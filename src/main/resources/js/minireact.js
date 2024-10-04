(function(r,p){typeof exports=="object"&&typeof module<"u"?p(exports):typeof define=="function"&&define.amd?define(["exports"],p):(r=typeof globalThis<"u"?globalThis:r||self,p(r.Dong={}))})(this,function(r){"use strict";function p(e,t,...n){return console.log("createElement生成的原始虚拟DOM如下"),e==null&&console.error("ERROR"),console.log({type:e,props:{...t,children:n.map(o=>typeof o=="object"?o:g(o))}}),{type:e,props:{...t,children:n.map(o=>typeof o=="object"?o:g(o))}}}function g(e){return{type:"TEXT_ELEMENT",props:{nodeValue:e,children:[]}}}let d=null,u=null,a=null,E=[],f=null,y=0;function j(e,t){u={dom:t,props:{children:[e]},alternate:a},d=u}requestIdleCallback(m);function m(e){let t=!1;for(;d&&!t;)d=N(d),t=e.timeRemaining()<1,t&&console.error("空闲时间耗尽，生成虚拟 DOM 被打断，等待下次调度以便从上次中断的地方继续");!d&&u&&R(),requestIdleCallback(m)}function N(e){if(f=e,typeof e.type=="function"&&(y=0,e.hooks=[]),M(e),e.child)return e.child;let t=e;for(;t;){if(t.sibling)return t.sibling;t=t.return}return null}function M(e){var t;if(typeof e.type=="function"){const n=e.type(e.props);Array.isArray(n)?T(e,n):T(e,[n])}else{e.dom||(e.dom=I(e));const n=((t=e.props)==null?void 0:t.children)||[];T(e,n)}}function q(e,t){if(e===t)return!0;const n=Object.keys(e),o=Object.keys(t);if(n.length!==o.length)return!1;for(let l of n){const c=e[l],s=t[l];if(l==="children"){if(Array.isArray(c)&&Array.isArray(s)){if(c.length!==s.length)return!1;if(c.length>1){for(let i=0;i<c.length;i++)if(c[i].type!==s[i].type)return!1}else if(c.length===1&&!Array.isArray(c[0]))return c[0].props.nodeValue===s[0].props.nodeValue}}else{if(l.startsWith("on")&&typeof c=="function"&&typeof s=="function")continue;if(c!==s)return!1}}return!0}function V(e,t){return e===t?!0:e.type!==t.type?!1:q(e.props,t.props)}function T(e,t){let n=0,o=null,l=e.alternate&&e.alternate.child,c=t.flat();for(;n<c.length||l!=null;){let s=c[n];typeof s!="object"&&(s=g(s));let i=null;l&&s&&s.type===l.type?!V(s,l)?(console.error("节点与上一课fiber树不一致,需要进行节点更新,更新的fiber如下"),i={type:l.type,props:s.props,dom:l.dom,return:e,alternate:l,effectTag:"UPDATE"},alert("节点与上一课fiber树不一致,需要进行节点更新："+JSON.stringify(i.props)),console.log(i),console.log(l)):i={type:l.type,props:s.props,dom:l.dom,return:e,alternate:l,effectTag:""}:(s&&(i={type:s.type,props:s.props,dom:null,return:e,alternate:null,effectTag:"PLACEMENT"}),l&&(l.effectTag="DELETION",E.push(l))),l&&(l=l.sibling),n===0?e.child=i:o&&(o.sibling=i),console.log("协调后的虚拟DOM如下"),o=i,console.log(o),n++}}function R(){E.forEach(h),E=[],h(u.child),A(u.child),a=u,u=null}function A(e){e&&(e.hooks&&e.hooks.forEach(t=>{t.effect&&t.hasEffect&&(t.cleanup=t.effect(),t.hasEffect=!1)}),A(e.child),A(e.sibling))}function h(e){if(!e)return;let t=e.return;for(;!t.dom;)t=t.return;const n=t.dom;if(e.effectTag==="PLACEMENT"&&e.dom!=null)n.appendChild(e.dom),console.log("节点插入");else if(e.effectTag==="UPDATE"&&e.dom!=null)U(e.dom,e.alternate.props,e.props);else if(e.effectTag==="DELETION"){O(e,n),console.log("节点删除");return}h(e.child),h(e.sibling)}function O(e,t){e&&(e.dom?t.removeChild(e.dom):O(e.child,t),O(e.sibling,t))}function I(e){let t;e.type=="TEXT_ELEMENT"?t=document.createTextNode(e.props.nodeValue):t=document.createElement(e.type==null?"div":e.type);for(const n in e.props)W(t,n,e.props[n]);return console.log("真实DOM如下:"),console.log(t),t}function k(e,t){return typeof t=="function"&&e.startsWith("on")}function _(e,t){return e=="style"&&typeof t=="object"}function D(e,t){return typeof t!="object"&&typeof t!="function"}const W=(e,t,n)=>{if(t!=="children")if(t==="nodeValue")e.textContent=n;else if(k(t,n)){const o=t.slice(2).toLowerCase();e.addEventListener(o,n)}else _(t,n)?Object.assign(e.style,n):D(t,n)&&e.setAttribute(t,n)};function U(e,t,n){if(e instanceof Text){t.nodeValue!==n.nodeValue&&(e.nodeValue=n.nodeValue);return}Object.keys(t).filter(k).forEach(o=>{const l=o.toLowerCase().substring(2);e.removeEventListener(l,t[o])}),Object.keys(n).filter(k).forEach(o=>{const l=o.toLowerCase().substring(2);e.addEventListener(l,n[o])}),Object.keys(t).filter(D).forEach(o=>{o in n||e.removeAttribute(o)}),Object.keys(n).filter(D).forEach(o=>{t[o]!==n[o]&&e.setAttribute(o,n[o])}),t.style&&Object.keys(t.style).forEach(o=>{(!n.style||!(o in n.style))&&(e.style[o]="")}),n.style&&Object.keys(n.style).forEach(o=>{(!t.style||t.style[o]!==n.style[o])&&(e.style[o]=n.style[o])})}function L(e){const t=f.alternate&&f.alternate.hooks?f.alternate.hooks[y]:null,n={state:t?t.state:e,queue:t?t.queue:[]};n.queue.forEach(l=>{console.log("处理hooks中"),console.log("action",l),n.state=l(n.state)}),n.queue.length=0;const o=l=>{console.log("setState调用"),n.queue.push(typeof l=="function"?l:()=>l),u={dom:a.dom,props:a.props,alternate:a},d=u,console.log("useState造成重新渲染"),requestIdleCallback(m)};return f.hooks.push(n),y++,[n.state,o]}function C(e,t){const n=f.alternate&&f.alternate.hooks?f.alternate.hooks[y]:null;let o;n&&t?o=t.some((c,s)=>!Object.is(c,n.deps[s])):o=!0;const l={deps:t,effect:e,cleanup:n?n.cleanup:null};if(o){l.cleanup&&l.cleanup();const c=e();l.cleanup=typeof c=="function"?c:null}f.hooks.push(l),y++}function S(){const e=new Set;function t(n,o){if(typeof o=="object"&&o!==null){if(e.has(o))return;e.add(o)}return o}return a==null?JSON.stringify(d,t,2):JSON.stringify(a,t,2)}const w={createElement:p,render:j,useState:L,useEffect:C,useAware:S};typeof window<"u"&&(window.Dong=w),r.default=w,r.render=j,r.useAware=S,r.useEffect=C,r.useState=L,Object.defineProperties(r,{__esModule:{value:!0},[Symbol.toStringTag]:{value:"Module"}})});
