# execute all lines: ctrl + shift + enter

library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))

overall <- read.table("exp_assembly_overall_energy_template_1652968342683.txt", 
                      header = F,
                      sep = " ",
                      col.names= c("overallx", "overally")
)

random <- read.table("exp_assembly_random_1652946312658.txt", 
                     header = F,
                     sep = " ",
                     col.names= c("randomx", "randomy")
)

res <- read.table("exp_assembly_residual_life_template_1652946380725.txt", 
                  header = F,
                  sep = " ",
                  col.names= c("resx","resy")
)

local <- read.table("exp_assembly_local_energy_template_1652950507165.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)

energyAware <- read.table("exp_assembly_energyAware_1652950789392.txt", 
                          header = F,
                          sep = " ",
                          col.names= c("enx","eny")
)


overall_x <- overall$overallx
overall_y <- overall$overally

random_y <- random$randomy
res_y <- res$resy
local_y <- local$locy
energyAware_y <- energyAware$eny

head(overall)

colors <- c("overall" = "#EEA236", "residual life" = "#5CB85C", "random" = "#46B8DA", "local"="gray64", "energyAware"="deeppink3")

ggplot(data=local, aes(x=overall_x, col = group)) + 
  
  geom_line(aes(y = overall_y, color = "overall"), size = 1) +
  geom_line(aes(y = res_y, color = "residual life"), size = 1) +
  geom_line(aes(y = random_y, color = "random"), size = 1) +
  geom_line(aes(y = local_y, color = "local"), size = 1) +
  geom_line(aes(y = energyAware_y, color = "energyAware"), size = 1) +
  
  labs(x = "round",
       y = "nodes availability",
       color = "Legend",
       title = "System availability in time  -  alpha 0.5, H 50", 
       caption = "simulation.cycles 12000, simulation.experiments 3, Network size 100, services per node 5, types 10") +
  
  scale_color_manual(values = colors)


