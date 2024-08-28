/*global module, window*/
/** @license
 * crossroads <http://millermedeiros.github.com/crossroads.js/>
 * Author: Miller Medeiros | MIT License
 * v0.12.0 (2013/01/21 13:47)
 */
(function () {
    'use strict';
    var factory = function () {

        // SignalBinding -------------------------------------------------
        //================================================================

        /**
         * Object that represents a binding between a Signal and a listener function.
         * <br />- <strong>This is an internal constructor and shouldn't be called by regular users.</strong>
         * <br />- inspired by Joa Ebert AS3 SignalBinding and Robert Penner's Slot classes.
         * @author Miller Medeiros
         * @constructor
         * @internal
         * @name SignalBinding
         * @param {Signal} signal Reference to Signal object that listener is currently bound to.
         * @param {Function} listener Handler function bound to the signal.
         * @param {boolean} isOnce If binding should be executed just once.
         * @param {Object} [listenerContext] Context on which listener will be executed (object that should represent the `this` variable inside listener function).
         * @param {Number} [priority] The priority level of the event listener. (default = 0).
         */
        function SignalBinding (signal, listener, isOnce, listenerContext, priority) {

            /**
             * Handler function bound to the signal.
             * @type Function
             * @private
             */
            this._listener = listener;

            /**
             * If binding should be executed just once.
             * @type boolean
             * @private
             */
            this._isOnce = isOnce;

            /**
             * Context on which listener will be executed (object that should represent the `this` variable inside listener function).
             * @memberOf SignalBinding.prototype
             * @name context
             * @type Object|undefined|null
             */
            this.context = listenerContext;

            /**
             * Reference to Signal object that listener is currently bound to.
             * @type Signal
             * @private
             */
            this._signal = signal;

            /**
             * Listener priority
             * @type Number
             * @private
             */
            this._priority = priority || 0;
        }

        SignalBinding.prototype = {

            /**
             * If binding is active and should be executed.
             * @type boolean
             */
            active: true,

            /**
             * Default parameters passed to listener during `Signal.dispatch` and `SignalBinding.execute`. (curried parameters)
             * @type Array|null
             */
            params: null,

            /**
             * Call listener passing arbitrary parameters.
             * <p>If binding was added using `Signal.addOnce()` it will be automatically removed from signal dispatch queue, this method is used internally for the signal dispatch.</p>
             * @param {Array} [paramsArr] Array of parameters that should be passed to the listener
             * @return {*} Value returned by the listener.
             */
            execute: function (paramsArr) {
                var handlerReturn, params;
                if (this.active && !!this._listener) {
                    params = this.params ? this.params.concat(paramsArr) : paramsArr;
                    handlerReturn = this._listener.apply(this.context, params);
                    if (this._isOnce) {
                        this.detach();
                    }
                }
                return handlerReturn;
            },

            /**
             * Detach binding from signal.
             * - alias to: mySignal.remove(myBinding.getListener());
             * @return {Function|null} Handler function bound to the signal or `null` if binding was previously detached.
             */
            detach: function () {
                return this.isBound() ? this._signal.remove(this._listener, this.context) : null;
            },

            /**
             * @return {Boolean} `true` if binding is still bound to the signal and have a listener.
             */
            isBound: function () {
                return (!!this._signal && !!this._listener);
            },

            /**
             * @return {Function} Handler function bound to the signal.
             */
            getListener: function () {
                return this._listener;
            },

            /**
             * Delete instance properties
             * @private
             */
            _destroy: function () {
                delete this._signal;
                delete this._listener;
                delete this.context;
            },

            /**
             * @return {boolean} If SignalBinding will only be executed once.
             */
            isOnce: function () {
                return this._isOnce;
            },

            /**
             * @return {string} String representation of the object.
             */
            toString: function () {
                return '[SignalBinding isOnce:' + this._isOnce + ', isBound:' + this.isBound() + ', active:' + this.active + ']';
            }

        };

        // Signal --------------------------------------------------------
        //================================================================

        function validateListener (listener, fnName) {
            if (typeof listener !== 'function') {
                throw new Error('listener is a required param of {fn}() and should be a Function.'.replace('{fn}', fnName));
            }
        }

        /**
         * Custom event broadcaster
         * <br />- inspired by Robert Penner's AS3 Signals.
         * @name Signal
         * @author Miller Medeiros
         * @constructor
         */
        function Signal () {
            /**
             * @type Array.<SignalBinding>
             * @private
             */
            this._bindings = [];
            this._prevParams = null;
        }

        Signal.prototype = {

            /**
             * Signals Version Number
             * @type String
             * @const
             */
            VERSION: '0.8.1',

            /**
             * If Signal should keep record of previously dispatched parameters and
             * automatically execute listener during `add()`/`addOnce()` if Signal was
             * already dispatched before.
             * @type boolean
             */
            memorize: false,

            /**
             * @type boolean
             * @private
             */
            _shouldPropagate: true,

            /**
             * If Signal is active and should broadcast events.
             * <p><strong>IMPORTANT:</strong> Setting this property during a dispatch will only affect the next dispatch, if you want to stop the propagation of a signal use `halt()` instead.</p>
             * @type boolean
             */
            active: true,

            /**
             * @param {Function} listener
             * @param {boolean} isOnce
             * @param {Object} [listenerContext]
             * @param {Number} [priority]
             * @return {SignalBinding}
             * @private
             */
            _registerListener: function (listener, isOnce, listenerContext, priority) {

                var prevIndex = this._indexOfListener(listener, listenerContext),
                    binding;

                if (prevIndex !== -1) {
                    binding = this._bindings[prevIndex];
                    if (binding.isOnce() !== isOnce) {
                        throw new Error('You cannot add' + (isOnce ? '' : 'Once') + '() then add' + (!isOnce ? '' : 'Once') + '() the same listener without removing the relationship first.');
                    }
                } else {
                    binding = new SignalBinding(this, listener, isOnce, listenerContext, priority);
                    this._addBinding(binding);
                }

                if (this.memorize && this._prevParams) {
                    binding.execute(this._prevParams);
                }

                return binding;
            },

            /**
             * @param {SignalBinding} binding
             * @private
             */
            _addBinding: function (binding) {
                //simplified insertion sort
                var n = this._bindings.length;
                do {
                    --n;
                } while (this._bindings[n] && binding._priority <= this._bindings[n]._priority);
                this._bindings.splice(n + 1, 0, binding);
            },

            /**
             * @param {Function} listener
             * @return {number}
             * @private
             */
            _indexOfListener: function (listener, context) {
                var n = this._bindings.length,
                    cur;
                while (n--) {
                    cur = this._bindings[n];
                    if (cur._listener === listener && cur.context === context) {
                        return n;
                    }
                }
                return -1;
            },

            /**
             * Check if listener was attached to Signal.
             * @param {Function} listener
             * @param {Object} [context]
             * @return {boolean} if Signal has the specified listener.
             */
            has: function (listener, context) {
                return this._indexOfListener(listener, context) !== -1;
            },

            /**
             * Add a listener to the signal.
             * @param {Function} listener Signal handler function.
             * @param {Object} [listenerContext] Context on which listener will be executed (object that should represent the `this` variable inside listener function).
             * @param {Number} [priority] The priority level of the event listener. Listeners with higher priority will be executed before listeners with lower priority. Listeners with same priority level will be executed at the same order as they were added. (default = 0)
             * @return {SignalBinding} An Object representing the binding between the Signal and listener.
             */
            add: function (listener, listenerContext, priority) {
                validateListener(listener, 'add');
                return this._registerListener(listener, false, listenerContext, priority);
            },

            /**
             * Add listener to the signal that should be removed after first execution (will be executed only once).
             * @param {Function} listener Signal handler function.
             * @param {Object} [listenerContext] Context on which listener will be executed (object that should represent the `this` variable inside listener function).
             * @param {Number} [priority] The priority level of the event listener. Listeners with higher priority will be executed before listeners with lower priority. Listeners with same priority level will be executed at the same order as they were added. (default = 0)
             * @return {SignalBinding} An Object representing the binding between the Signal and listener.
             */
            addOnce: function (listener, listenerContext, priority) {
                validateListener(listener, 'addOnce');
                return this._registerListener(listener, true, listenerContext, priority);
            },

            /**
             * Remove a single listener from the dispatch queue.
             * @param {Function} listener Handler function that should be removed.
             * @param {Object} [context] Execution context (since you can add the same handler multiple times if executing in a different context).
             * @return {Function} Listener handler function.
             */
            remove: function (listener, context) {
                validateListener(listener, 'remove');

                var i = this._indexOfListener(listener, context);
                if (i !== -1) {
                    this._bindings[i]._destroy(); //no reason to a SignalBinding exist if it isn't attached to a signal
                    this._bindings.splice(i, 1);
                }
                return listener;
            },

            /**
             * Remove all listeners from the Signal.
             */
            removeAll: function () {
                var n = this._bindings.length;
                while (n--) {
                    this._bindings[n]._destroy();
                }
                this._bindings.length = 0;
            },

            /**
             * @return {number} Number of listeners attached to the Signal.
             */
            getNumListeners: function () {
                return this._bindings.length;
            },

            /**
             * Stop propagation of the event, blocking the dispatch to next listeners on the queue.
             * <p><strong>IMPORTANT:</strong> should be called only during signal dispatch, calling it before/after dispatch won't affect signal broadcast.</p>
             * @see Signal.prototype.disable
             */
            halt: function () {
                this._shouldPropagate = false;
            },

            /**
             * Dispatch/Broadcast Signal to all listeners added to the queue.
             * @param {...*} [arguments] Parameters that should be passed to each handler.
             */
            dispatch: function () {
                if (!this.active) {
                    return;
                }

                var paramsArr = Array.prototype.slice.call(arguments),
                    n = this._bindings.length,
                    bindings;

                if (this.memorize) {
                    this._prevParams = paramsArr;
                }

                if (!n) {
                    //should come after memorize
                    return;
                }

                bindings = this._bindings.slice(); //clone array in case add/remove items during dispatch
                this._shouldPropagate = true; //in case `halt` was called before dispatch or during the previous dispatch.

                //execute all callbacks until end of the list or until a callback returns `false` or stops propagation
                //reverse loop since listeners with higher priority will be added at the end of the list
                do {
                    n--;
                } while (bindings[n] && this._shouldPropagate && bindings[n].execute(paramsArr) !== false);
            },

            /**
             * Forget memorized arguments.
             * @see Signal.memorize
             */
            forget: function () {
                this._prevParams = null;
            },

            /**
             * Remove all bindings from signal and destroy any reference to external objects (destroy Signal object).
             * <p><strong>IMPORTANT:</strong> calling any method on the signal instance after calling dispose will throw errors.</p>
             */
            dispose: function () {
                this.removeAll();
                delete this._bindings;
                delete this._prevParams;
            },

            /**
             * @return {string} String representation of the object.
             */
            toString: function () {
                return '[Signal active:' + this.active + ' numListeners:' + this.getNumListeners() + ']';
            }

        };


        // Namespace -----------------------------------------------------
        //================================================================

        /**
         * Signals namespace
         * @namespace
         * @name signals
         */
        var signals = Signal;

        /**
         * Custom event broadcaster
         * @see Signal
         */
            // alias for backwards compatibility (see #gh-44)
        signals.Signal = Signal;


        //================================================================
        // Crossroads Factory --------------------------------------------
        //================================================================

        var crossroads,
            _hasOptionalGroupBug,
            UNDEF;

        // Helpers -----------
        //====================

        // IE 7-8 capture optional groups as empty strings while other browsers
        // capture as `undefined`
        _hasOptionalGroupBug = (/t(.+)?/).exec('t')[1] === '';

        function arrayIndexOf (arr, val) {
            if (arr.indexOf) {
                return arr.indexOf(val);
            } else {
                //Array.indexOf doesn't work on IE 6-7
                var n = arr.length;
                while (n--) {
                    if (arr[n] === val) {
                        return n;
                    }
                }
                return -1;
            }
        }

        function arrayRemove (arr, item) {
            var i = arrayIndexOf(arr, item);
            if (i !== -1) {
                arr.splice(i, 1);
            }
        }

        function isKind (val, kind) {
            return '[object ' + kind + ']' === Object.prototype.toString.call(val);
        }

        function isRegExp (val) {
            return isKind(val, 'RegExp');
        }

        function isArray (val) {
            return isKind(val, 'Array');
        }

        function isFunction (val) {
            return typeof val === 'function';
        }

        //borrowed from AMD-utils
        function typecastValue (val) {
            var r;
            if (val === null || val === 'null') {
                r = null;
            } else if (val === 'true') {
                r = true;
            } else if (val === 'false') {
                r = false;
            } else if (val === UNDEF || val === 'undefined') {
                r = UNDEF;
            } else if (val === '' || isNaN(val)) {
                //isNaN('') returns false
                r = val;
            } else {
                //parseFloat(null || '') returns NaN
                r = parseFloat(val);
            }
            return r;
        }

        function typecastArrayValues (values) {
            var n = values.length,
                result = [];
            while (n--) {
                result[n] = typecastValue(values[n]);
            }
            return result;
        }

        //borrowed from AMD-Utils
        function decodeQueryString (str, shouldTypecast) {
            var queryArr = (str || '').replace('?', '').split('&'),
                n = queryArr.length,
                obj = {},
                item, val;
            while (n--) {
                item = queryArr[n].split('=');
                val = shouldTypecast ? typecastValue(item[1]) : item[1];
                obj[item[0]] = (typeof val === 'string') ? decodeURIComponent(val) : val;
            }
            return obj;
        }


        // Crossroads --------
        //====================

        /**
         * @constructor
         */
        function Crossroads () {
            this.bypassed = new signals.Signal();
            this.routed = new signals.Signal();
            this._routes = [];
            this._prevRoutes = [];
            this._piped = [];
            this.resetState();
        }

        Crossroads.prototype = {

            greedy: false,

            greedyEnabled: true,

            ignoreCase: true,

            ignoreState: false,

            shouldTypecast: false,

            normalizeFn: null,

            resetState: function () {
                this._prevRoutes.length = 0;
                this._prevMatchedRequest = null;
                this._prevBypassedRequest = null;
            },

            create: function () {
                return new Crossroads();
            },

            addRoute: function (pattern, callback, priority) {
                var route = new Route(pattern, callback, priority, this);
                this._sortedInsert(route);
                return route;
            },

            removeRoute: function (route) {
                arrayRemove(this._routes, route);
                route._destroy();
            },

            removeAllRoutes: function () {
                var n = this.getNumRoutes();
                while (n--) {
                    this._routes[n]._destroy();
                }
                this._routes.length = 0;
            },

            parse: function (request, defaultArgs) {
                request = request || '';
                defaultArgs = defaultArgs || [];

                // should only care about different requests if ignoreState isn't true
                if (!this.ignoreState &&
                    (request === this._prevMatchedRequest ||
                        request === this._prevBypassedRequest)) {
                    return;
                }

                var routes = this._getMatchedRoutes(request),
                    i = 0,
                    n = routes.length,
                    cur;

                if (n) {
                    this._prevMatchedRequest = request;

                    this._notifyPrevRoutes(routes, request);
                    this._prevRoutes = routes;
                    //should be incremental loop, execute routes in order
                    while (i < n) {
                        cur = routes[i];
                        cur.route.matched.dispatch.apply(cur.route.matched, defaultArgs.concat(cur.params));
                        cur.isFirst = !i;
                        this.routed.dispatch.apply(this.routed, defaultArgs.concat([request, cur]));
                        i += 1;
                    }
                } else {
                    this._prevBypassedRequest = request;
                    this.bypassed.dispatch.apply(this.bypassed, defaultArgs.concat([request]));
                }

                this._pipeParse(request, defaultArgs);
            },

            _notifyPrevRoutes: function (matchedRoutes, request) {
                var i = 0, prev;
                prev = this._prevRoutes[i++];
                while (prev) {
                    //check if switched exist since route may be disposed
                    if (prev.route.switched && this._didSwitch(prev.route, matchedRoutes)) {
                        prev.route.switched.dispatch(request);
                    }
                    prev = this._prevRoutes[i++];
                }
            },

            _didSwitch: function (route, matchedRoutes) {
                var matched,
                    i = 0;
                matched = matchedRoutes[i++];
                while (matched) {
                    // only dispatch switched if it is going to a different route
                    if (matched.route === route) {
                        return false;
                    }
                    matched = matchedRoutes[i++];
                }
                return true;
            },

            _pipeParse: function (request, defaultArgs) {
                var i = 0, route;
                route = this._piped[i++];
                while (route) {
                    route.parse(request, defaultArgs);
                    route = this._piped[i++];
                }
            },

            getNumRoutes: function () {
                return this._routes.length;
            },

            _sortedInsert: function (route) {
                //simplified insertion sort
                var routes = this._routes,
                    n = routes.length;
                do {
                    --n;
                } while (routes[n] && route._priority <= routes[n]._priority);
                routes.splice(n + 1, 0, route);
            },

            _getMatchedRoutes: function (request) {
                var res = [],
                    routes = this._routes,
                    n = routes.length,
                    route;
                //should be decrement loop since higher priorities are added at the end of array
                route = routes[--n];
                while (route) {
                    if ((!res.length || this.greedy || route.greedy) && route.match(request)) {
                        res.push({
                            route: route,
                            params: route._getParamsArray(request)
                        });
                    }
                    if (!this.greedyEnabled && res.length) {
                        break;
                    }
                    route = routes[--n];
                }
                return res;
            },

            pipe: function (otherRouter) {
                this._piped.push(otherRouter);
            },

            unpipe: function (otherRouter) {
                arrayRemove(this._piped, otherRouter);
            },

            toString: function () {
                return '[crossroads numRoutes:' + this.getNumRoutes() + ']';
            }
        };

        //"static" instance
        crossroads = new Crossroads();
        crossroads.VERSION = '0.12.0';

        crossroads.NORM_AS_ARRAY = function (req, vals) {
            return [vals.vals_];
        };

        crossroads.NORM_AS_OBJECT = function (req, vals) {
            return [vals];
        };


        // Route --------------
        //=====================

        /**
         * @constructor
         */
        function Route (pattern, callback, priority, router) {
            var isRegexPattern = isRegExp(pattern),
                patternLexer = router.patternLexer;
            this._router = router;
            this._pattern = pattern;
            this._paramsIds = isRegexPattern ? null : patternLexer.getParamIds(pattern);
            this._optionalParamsIds = isRegexPattern ? null : patternLexer.getOptionalParamsIds(pattern);
            this._matchRegexp = isRegexPattern ? pattern : patternLexer.compilePattern(pattern, router.ignoreCase);
            this.matched = new signals.Signal();
            this.switched = new signals.Signal();
            if (callback) {
                this.matched.add(callback);
            }
            this._priority = priority || 0;
        }

        Route.prototype = {

            greedy: false,

            rules: void(0),

            match: function (request) {
                request = request || '';
                return this._matchRegexp.test(request) && this._validateParams(request); //validate params even if regexp because of `request_` rule.
            },

            _validateParams: function (request) {
                var rules = this.rules,
                    values = this._getParamsObject(request),
                    key;
                for (key in rules) {
                    // normalize_ isn't a validation rule... (#39)
                    if (key !== 'normalize_' && rules.hasOwnProperty(key) && !this._isValidParam(request, key, values)) {
                        return false;
                    }
                }
                return true;
            },

            _isValidParam: function (request, prop, values) {
                var validationRule = this.rules[prop],
                    val = values[prop],
                    isValid = false,
                    isQuery = (prop.indexOf('?') === 0);

                if (val == null && this._optionalParamsIds && arrayIndexOf(this._optionalParamsIds, prop) !== -1) {
                    isValid = true;
                }
                else if (isRegExp(validationRule)) {
                    if (isQuery) {
                        val = values[prop + '_']; //use raw string
                    }
                    isValid = validationRule.test(val);
                }
                else if (isArray(validationRule)) {
                    if (isQuery) {
                        val = values[prop + '_']; //use raw string
                    }
                    isValid = this._isValidArrayRule(validationRule, val);
                }
                else if (isFunction(validationRule)) {
                    isValid = validationRule(val, request, values);
                }

                return isValid; //fail silently if validationRule is from an unsupported type
            },

            _isValidArrayRule: function (arr, val) {
                if (!this._router.ignoreCase) {
                    return arrayIndexOf(arr, val) !== -1;
                }

                if (typeof val === 'string') {
                    val = val.toLowerCase();
                }

                var n = arr.length,
                    item,
                    compareVal;

                while (n--) {
                    item = arr[n];
                    compareVal = (typeof item === 'string') ? item.toLowerCase() : item;
                    if (compareVal === val) {
                        return true;
                    }
                }
                return false;
            },

            _getParamsObject: function (request) {
                var shouldTypecast = this._router.shouldTypecast,
                    values = this._router.patternLexer.getParamValues(request, this._matchRegexp, shouldTypecast),
                    o = {},
                    n = values.length,
                    param, val;
                while (n--) {
                    val = values[n];
                    if (this._paramsIds) {
                        param = this._paramsIds[n];
                        if (param.indexOf('?') === 0 && val) {
                            //make a copy of the original string so array and
                            //RegExp validation can be applied properly
                            o[param + '_'] = val;
                            //update vals_ array as well since it will be used
                            //during dispatch
                            val = decodeQueryString(val, shouldTypecast);
                            values[n] = val;
                        }
                        // IE will capture optional groups as empty strings while other
                        // browsers will capture `undefined` so normalize behavior.
                        // see: #gh-58, #gh-59, #gh-60
                        if (_hasOptionalGroupBug && val === '' && arrayIndexOf(this._optionalParamsIds, param) !== -1) {
                            val = void(0);
                            values[n] = val;
                        }
                        o[param] = val;
                    }
                    //alias to paths and for RegExp pattern
                    o[n] = val;
                }
                o.request_ = shouldTypecast ? typecastValue(request) : request;
                o.vals_ = values;
                return o;
            },

            _getParamsArray: function (request) {
                var norm = this.rules ? this.rules.normalize_ : null,
                    params;
                norm = norm || this._router.normalizeFn; // default normalize
                if (norm && isFunction(norm)) {
                    params = norm(request, this._getParamsObject(request));
                } else {
                    params = this._getParamsObject(request).vals_;
                }
                return params;
            },

            interpolate: function (replacements) {
                var str = this._router.patternLexer.interpolate(this._pattern, replacements);
                if (!this._validateParams(str)) {
                    throw new Error('Generated string doesn\'t validate against `Route.rules`.');
                }
                return str;
            },

            dispose: function () {
                this._router.removeRoute(this);
            },

            _destroy: function () {
                this.matched.dispose();
                this.switched.dispose();
                this.matched = this.switched = this._pattern = this._matchRegexp = null;
            },

            toString: function () {
                return '[Route pattern:"' + this._pattern + '", numListeners:' + this.matched.getNumListeners() + ']';
            }

        };


        // Pattern Lexer ------
        //=====================

        Crossroads.prototype.patternLexer = (function () {

            var
            //match chars that should be escaped on string regexp
                ESCAPE_CHARS_REGEXP = /[\\.+*?\^$\[\](){}\/'#]/g,

            //trailing slashes (begin/end of string)
                LOOSE_SLASHES_REGEXP = /^\/|\/$/g,
                LEGACY_SLASHES_REGEXP = /\/$/g,

            //params - everything between `{ }` or `: :`
                PARAMS_REGEXP = /(?:\{|:)([^}:]+)(?:\}|:)/g,

            //used to save params during compile (avoid escaping things that
            //shouldn't be escaped).
                TOKENS = {
                    'OS': {
                        //optional slashes
                        //slash between `::` or `}:` or `\w:` or `:{?` or `}{?` or `\w{?`
                        rgx: /([:}]|\w(?=\/))\/?(:|(?:\{\?))/g,
                        save: '$1{{id}}$2',
                        res: '\\/?'
                    },
                    'RS': {
                        //required slashes
                        //used to insert slash between `:{` and `}{`
                        rgx: /([:}])\/?(\{)/g,
                        save: '$1{{id}}$2',
                        res: '\\/'
                    },
                    'RQ': {
                        //required query string - everything in between `{? }`
                        rgx: /\{\?([^}]+)\}/g,
                        //everything from `?` till `#` or end of string
                        res: '\\?([^#]+)'
                    },
                    'OQ': {
                        //optional query string - everything in between `:? :`
                        rgx: /:\?([^:]+):/g,
                        //everything from `?` till `#` or end of string
                        res: '(?:\\?([^#]*))?'
                    },
                    'OR': {
                        //optional rest - everything in between `: *:`
                        rgx: /:([^:]+)\*:/g,
                        res: '(.*)?' // optional group to avoid passing empty string as captured
                    },
                    'RR': {
                        //rest param - everything in between `{ *}`
                        rgx: /\{([^}]+)\*\}/g,
                        res: '(.+)'
                    },
                    // required/optional params should come after rest segments
                    'RP': {
                        //required params - everything between `{ }`
                        rgx: /\{([^}]+)\}/g,
                        res: '([^\\/?]+)'
                    },
                    'OP': {
                        //optional params - everything between `: :`
                        rgx: /:([^:]+):/g,
                        res: '([^\\/?]+)?\/?'
                    }
                },

                LOOSE_SLASH = 1,
                STRICT_SLASH = 2,
                LEGACY_SLASH = 3,

                _slashMode = LOOSE_SLASH;


            function precompileTokens () {
                var key, cur;
                for (key in TOKENS) {
                    if (TOKENS.hasOwnProperty(key)) {
                        cur = TOKENS[key];
                        cur.id = '__CR_' + key + '__';
                        cur.save = ('save' in cur) ? cur.save.replace('{{id}}', cur.id) : cur.id;
                        cur.rRestore = new RegExp(cur.id, 'g');
                    }
                }
            }

            precompileTokens();


            function captureVals (regex, pattern) {
                var vals = [], match;
                // very important to reset lastIndex since RegExp can have "g" flag
                // and multiple runs might affect the result, specially if matching
                // same string multiple times on IE 7-8
                regex.lastIndex = 0;
                match = regex.exec(pattern);
                while (match) {
                    vals.push(match[1]);
                    match = regex.exec(pattern);
                }
                return vals;
            }

            function getParamIds (pattern) {
                return captureVals(PARAMS_REGEXP, pattern);
            }

            function getOptionalParamsIds (pattern) {
                return captureVals(TOKENS.OP.rgx, pattern);
            }

            function compilePattern (pattern, ignoreCase) {
                pattern = pattern || '';

                if (pattern) {
                    if (_slashMode === LOOSE_SLASH) {
                        pattern = pattern.replace(LOOSE_SLASHES_REGEXP, '');
                    }
                    else if (_slashMode === LEGACY_SLASH) {
                        pattern = pattern.replace(LEGACY_SLASHES_REGEXP, '');
                    }

                    //save tokens
                    pattern = replaceTokens(pattern, 'rgx', 'save');
                    //regexp escape
                    pattern = pattern.replace(ESCAPE_CHARS_REGEXP, '\\$&');
                    //restore tokens
                    pattern = replaceTokens(pattern, 'rRestore', 'res');

                    if (_slashMode === LOOSE_SLASH) {
                        pattern = '\\/?' + pattern;
                    }
                }

                if (_slashMode !== STRICT_SLASH) {
                    //single slash is treated as empty and end slash is optional
                    pattern += '\\/?';
                }
                return new RegExp('^' + pattern + '$', ignoreCase ? 'i' : '');
            }

            function replaceTokens (pattern, regexpName, replaceName) {
                var cur, key;
                for (key in TOKENS) {
                    if (TOKENS.hasOwnProperty(key)) {
                        cur = TOKENS[key];
                        pattern = pattern.replace(cur[regexpName], cur[replaceName]);
                    }
                }
                return pattern;
            }

            function getParamValues (request, regexp, shouldTypecast) {
                var vals = regexp.exec(request);
                if (vals) {
                    vals.shift();
                    if (shouldTypecast) {
                        vals = typecastArrayValues(vals);
                    }
                }
                return vals;
            }

            function interpolate (pattern, replacements) {
                if (typeof pattern !== 'string') {
                    throw new Error('Route pattern should be a string.');
                }

                var replaceFn = function (match, prop) {
                    var val;
                    prop = (prop.substr(0, 1) === '?') ? prop.substr(1) : prop;
                    if (replacements[prop] != null) {
                        if (typeof replacements[prop] === 'object') {
                            var queryParts = [];
                            for (var key in replacements[prop]) {
                                if (replacements[prop].hasProperty(key)) {
                                    queryParts.push(encodeURI(key + '=' + replacements[prop][key]));
                                }
                            }
                            val = '?' + queryParts.join('&');
                        } else {
                            // make sure value is a string see #gh-54
                            val = String(replacements[prop]);
                        }

                        if (match.indexOf('*') === -1 && val.indexOf('/') !== -1) {
                            throw new Error('Invalid value "' + val + '" for segment "' + match + '".');
                        }
                    }
                    else if (match.indexOf('{') !== -1) {
                        throw new Error('The segment ' + match + ' is required.');
                    }
                    else {
                        val = '';
                    }
                    return val;
                };

                if (!TOKENS.OS.trail) {
                    TOKENS.OS.trail = new RegExp('(?:' + TOKENS.OS.id + ')+$');
                }

                return pattern
                    .replace(TOKENS.OS.rgx, TOKENS.OS.save)
                    .replace(PARAMS_REGEXP, replaceFn)
                    .replace(TOKENS.OS.trail, '') // remove trailing
                    .replace(TOKENS.OS.rRestore, '/'); // add slash between segments
            }

            //API
            return {
                strict: function () {
                    _slashMode = STRICT_SLASH;
                },
                loose: function () {
                    _slashMode = LOOSE_SLASH;
                },
                legacy: function () {
                    _slashMode = LEGACY_SLASH;
                },
                getParamIds: getParamIds,
                getOptionalParamsIds: getOptionalParamsIds,
                getParamValues: getParamValues,
                compilePattern: compilePattern,
                interpolate: interpolate
            };

        }());


        return crossroads;
    };

    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else if (typeof module !== 'undefined' && module.exports) { //Node
        module.exports = factory();
    } else {
        /*jshint sub:true */
        window['crossroads'] = factory();
    }

}());

