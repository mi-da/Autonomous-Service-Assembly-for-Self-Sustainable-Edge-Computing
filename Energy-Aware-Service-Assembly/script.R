library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))

#local <- read.table("exp_assembly_local_energy_template_1647011129708.txt", 
#                header = F,
#               sep = " ",
#               col.names= c("localx", "localy")
#               )

overall <- read.table("exp_assembly_overall_energy_template_1647857935731.txt", 
                 header = F,
                 sep = " ",
                 col.names= c("overallx", "overally")
                 )

overall_sfr <- read.table("exp_assembly_overall_energy_template_1647857809805.txt", 
                      header = F,
                      sep = " ",
                      col.names= c("overallsfrx", "overallsfry")
)

overall_afr <- read.table("exp_assembly_overall_energy_template_1647857601759.txt", 
                          header = F,
                          sep = " ",
                          col.names= c("overallafrx", "overallafry")
)

#random <- read.table("exp_assembly_random_1646417411428.txt", 
#                     header = F,
#                     sep = " ",
#                     col.names= c("randomx", "randomy")
#                 )

#wrandom <- read.table("exp_assembly_residual_life_template_1647010630236.txt", 
#                    header = F,
#                    sep = " ",
#                    col.names= c("randomx", "wrandomy")
#                 )

#energyAware <- read.table("exp_assembly_energyAware_1647011173616.txt", 
#                     header = F,
#                     sep = " ",
#                     col.names= c("resx", "resy")
#                 )

#local_x <- local$localx
#local_y <- local$localy

overall_x <- overall$overallx
overall_y <- overall$overally

overallsfr_x <- overall_sfr$overallsfrx
overallsfr_y <- overall_sfr$overallsfry

overallafr_x <- overall_afr$overallafrx
overallafr_y <- overall_afr$overallafry

#random_x <- random$randomx
#random_y <- random$randomy

#wrandom_x <- wrandom$wrandomx
#wrandom_y <- wrandom$wrandomy

#energyAware_x <- residual_life$resx
#energyAware_y <- residual_life$resy

head(overall)

ggplot(data=local, aes(x=overall_x)) + 
  
  
  #geom_line(aes(y = local_y, color = "#D43F3A"), color = "#D43F3A", size = 1) +
  geom_line(aes(y = overall_y, color = "#EEA236"), color = "#EEA236", size = 1) +
  geom_line(aes(y = overallsfr_y, color = "#5CB85C"), color = "#5CB85C", size = 1) +
  geom_line(aes(y = overallafr_y, color = "#46B8DA"), color = "#46B8DA", size = 1) +
  #geom_line(aes(y = random_y, color = "#5CB85C"), color = "#5CB85C", size = 1) +
  #geom_line(aes(y = wrandom_y, color = "#46B8DA"), color = "#46B8DA", size = 1) +
  #geom_line(aes(y = energyAware_y, color = "#9632B8"), size = 1) +
  
  
  scale_x_continuous(breaks=seq(0,100,by=5))+
  scale_y_continuous(breaks=seq(0,1,by=0.1))+

  labs(x = "round", y = "availability", color = "Legend") +
  #scale_color_manual(name = "Strategy", 
  #                   values = c("local" = "#D43F3A", "overall" = "#EEA236", "random" = "#5CB85C", "residual life"="#46B8DA", "energy aware"="grey")) +
  
  scale_color_manual(name = "Strategy", 
                     values = c("overall (availability)" = "#EEA236", "overall (services fully res.)" = "#5CB85C", "overall (assemblies fully res.)" = "#46B8DA")) +
  
  
  labs(title = "System availability in time", caption = "simulation.cycles 600, simulation.experiments 2, Network size 100, services per node 5, types 10, alpha 1, H 1")


