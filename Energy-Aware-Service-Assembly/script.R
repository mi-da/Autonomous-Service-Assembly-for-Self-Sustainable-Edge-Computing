# execute all lines: ctrl + shift + enter

library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))

overall <- read.table("exp_assembly_overall_energy_template_1664433597005.txt", 
                      header = F,
                      sep = " ",
                      col.names= c("overallx", "overally")
)

random <- read.table("exp_assembly_random_1664434657959.txt", 
                     header = F,
                     sep = " ",
                     col.names= c("randomx", "randomy")
)

res <- read.table("exp_assembly_residual_life_template_1664435107166.txt", 
                  header = F,
                  sep = " ",
                  col.names= c("resx","resy")
)

local <- read.table("exp_assembly_local_energy_template_1664433626182.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)

learning <- read.table("exp_assembly_energyAware_1664434759956.txt", 
                          header = F,
                          sep = " ",
                          col.names= c("enx","eny")
)


overall_x <- overall$overallx
overall_y <- overall$overally

random_y <- random$randomy
res_y <- res$resy
local_y <- local$locy
energyAware_y <- learning$eny

head(overall)

colors <- c("overall" = "#EEA236", "residual life" = "#5CB85C", "random" = "#46B8DA", "local"="gray64", "learning"="deeppink3")

ggplot(data=local, aes(x=overall_x, col = group)) + 
  
  geom_line(aes(y = overall_y, color = "overall"), size = 1) +
  geom_line(aes(y = res_y, color = "residual life"), size = 1) +
  geom_line(aes(y = random_y, color = "random"), size = 1) +
  geom_line(aes(y = local_y, color = "local"), size = 1) +
  geom_line(aes(y = energyAware_y, color = "learning"), size = 1) +
  
  labs(x = "round",
       y = "nodes availability",
       color = "Legend",
       title = "System availability in time  -  alpha 0.5, H 50", 
       caption = "simulation.cycles 12000, simulation.experiments 3, Network size 100, services per node 5, types 10") +
  
  scale_color_manual(values = colors)


