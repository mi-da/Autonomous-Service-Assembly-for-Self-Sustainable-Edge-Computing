# execute all lines: ctrl + shift + enter

library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))


local <- read.table("/Users/francescaricci/Desktop/FQoS/small battery/exp_assembly_local_energy_template_1675851065806.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)


local_x <- local$locx
local_y <- local$locy

head(local)

colors <- c("local"="red")

ggplot(data=local, aes(x=local_x, col = group)) + 
  
  geom_line(aes(y = local_y, color = "local"), size = 1) +
  
  labs(x = "round",
       y = "nodes availability",
       color = "Legend",
       title = "FQoS in time (B, small battery) -  alpha 0.5, H 50", 
       caption = "simulation.cycles 12000, simulation.experiments 50, Network size 50, services per node 5, types 10") +
  
  scale_color_manual(values = colors)


