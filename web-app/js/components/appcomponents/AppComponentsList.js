/*
 * Copyright 2013 Next Century Corporation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 ;(function () {
    
    Ozone.components.appcomponents = Ozone.components.appcomponents || {};

    var SuperClass = Ozone.components.BaseView,
        DetailsTip = Ozone.components.appcomponents.DetailsTip,
        tip;

    Ozone.components.appcomponents.AppComponentsList = SuperClass.extend({

        // managed sub views
        views: null,

        searchQuery: '',

        addFilterFn: null,

        selectable: true,
        selectClass: 'selected',
        selected: null,

        events: {
            'click .widget': '_onClick',
            'click .widget-details': '_showDetailsTip',
            'mouseenter .widget': '_showDetailsTipOption',
            'mouseleave .widget': '_hideDetailsOption'
        },

        // boolean flag indicating whether or not to show details link
        details: true,

        // array of managed views
        views: null,

        // collection of all app components person has access to
        allAppComponents: null,

        initialize: function () {
            SuperClass.prototype.initialize.apply(this, arguments);
            _.bindAll(this, 'addOne');

            this.allAppComponents = this.collection;

            var standardAppComponents = this.allAppComponents.filter(function(appComponent) {
                return appComponent.get('widgetTypes')[0].name === 'standard';
            });

            this.collection = new Ozone.data.collections.Widgets(standardAppComponents);
        },

        render: function () {
            this.addAll();
            this.$el.disableSelection();
            return this;
        },

        addAll: function () {
            _.invoke(this.views, 'remove');
            this.views = [];
            this.collection.each(this.addOne);
            return this;
        },

        addOne: function (model, index) {
            if(_.isFunction(this.addFilterFn) && !this.addFilterFn(model, index)) {
                return;
            }

            var view = new Ozone.components.appcomponents.AppComponent({
                details: this.details,
                model: model
            });
            this.views.push(view);

            this.$el.append(view.render().$el);
        },

        filter: function (query) {
            this.searchQuery = query;
            return this.addAll();
        },

        removeAppComponent: function (view, tip) {
            var model = view.model;

            // remove model from internal filtered collection
            this.collection.remove(model);

            // remove model from global collection
            this.allAppComponents.remove(model);

            // remove view and tip
            view.remove();
            this._removeDetailsTip();
        },

        remove: function () {
            _.invoke(this.views, 'remove');
            this.views = null;

            this._removeDetailsTip();

            this.$el.enableSelection()

            return SuperClass.prototype.remove.call(this);
        },

        _onClick: function (evt) {
            if(this.selectable) {
                var view = $(evt.currentTarget).data('view');

                if(this.selected) {
                    this.selected.$el.removeClass(this.selectClass);
                }
                this.selected = view;
                this.selected.$el.addClass(this.selectClass);
            }
        },

        _showDetailsTip: function (evt) {
            var me = this,
                view = $(evt.currentTarget).parent().data('view');

            evt.preventDefault();
            evt.stopPropagation();

            this._removeDetailsTip();
            
            tip = new DetailsTip({
                model: view.model
            });

            tip.render().$el
                .appendTo('body')
                .position({
                    my: 'top',
                    at: 'bottom',
                    of: view.$el
                })
                .on('click', '.widget-remove', function(evt) {
                    evt.preventDefault();
                    me.removeAppComponent(view, tip);
                });
            tip.shown();
        },

        _removeDetailsTip: function () {
            tip && tip.remove();
            tip = null;
        },

        _showDetailsTipOption: function (evt) {
            this.$el.find('.widget-details').css('visibility', 'hidden');
            $(evt.currentTarget).children('.widget-details').css('visibility', '');
        },

        _hideDetailsOption: function (evt) {
            $(evt.currentTarget).children('.widget-details').css('visibility', 'hidden');
        }

    });

})();