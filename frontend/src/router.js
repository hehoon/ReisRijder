import { createRouter, createWebHistory  } from "vue-router";
import HomeComponent from "@/components/HomeComponent";
import NotFoundComponent from "@/components/NotFoundComponent";

export default createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: "/",
            component: HomeComponent
        },
        {
            path: "/:pathMatch(.*)*",
            component: NotFoundComponent
        }
    ]
});