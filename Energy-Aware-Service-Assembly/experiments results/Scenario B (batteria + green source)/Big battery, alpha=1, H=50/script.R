# execute all lines: ctrl + shift + enter

library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))

overall <- read.table("exp_assembly_overall_energy_template.txt", 
                      header = F,
                      sep = " ",
                      col.names= c("overallx", "overally")
)

random <- read.table("exp_assembly_random.txt", 
                     header = F,
                     sep = " ",
                     col.names= c("randomx", "randomy")
)

res <- read.table("exp_assembly_residual_life_template.txt", 
                  header = F,
                  sep = " ",
                  col.names= c("resx","resy")
)

local <- read.table("exp_assembly_local_energy_template.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)

learning <- read.table("exp_assembly_energyAware.txt", 
                          header = F,
                          sep = " ",
                          col.names= c("enx","eny")
)

qos <- read.table("exp_assembly_QoS.txt", 
                       header = F,
                       sep = " ",
                       col.names= c("qosx","qosy")
)


overall_x <- overall$overallx
overall_y <- overall$overally

random_y <- random$randomy
res_y <- res$resy
local_y <- local$locy
energyAware_y <- learning$eny
qos_y <- qos$qosy

head(overall)

colors <- c("overall" = "#EEA236", "residual life" = "#5CB85C", "random" = "#46B8DA", "local"="gray64", "energy balance"="deeppink3", "qos"="#FF0000")

ggplot(data=local, aes(x=overall_x, col = group)) + 
  
  geom_line(aes(y = overall_y, color = "overall"), size = 1) +
  geom_line(aes(y = res_y, color = "residual life"), size = 1) +
  geom_line(aes(y = random_y, color = "random"), size = 1) +
  geom_line(aes(y = local_y, color = "local"), size = 1) +
  geom_line(aes(y = energyAware_y, color = "energy balance"), size = 1) +
  geom_line(aes(y = qos_y, color = "qos"), size = 1) +
  
  labs(x = "Learning cycle",
       y = "Instantaneous infrastructure availability",
       color = "Legend",
       title = "Instantaneous infrastructure availability - Big battery, alpha=1, H=50", 
       caption = "Learning cycles=1000, Simulation experiments=10, Network size=50, services per node=5, types=10") +
  
  scale_color_manual(values = colors)
